package com.nhnacademy.bookstore.member.pointRecord.service.impl;

import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.pointRecord.PointRecord;
import com.nhnacademy.bookstore.entity.purchase.Purchase;
import com.nhnacademy.bookstore.member.member.exception.MemberNotExistsException;
import com.nhnacademy.bookstore.member.member.repository.MemberRepository;
import com.nhnacademy.bookstore.member.member.service.MemberPointService;
import com.nhnacademy.bookstore.member.pointRecord.dto.response.ReadPointRecordResponse;
import com.nhnacademy.bookstore.member.pointRecord.exception.NoBuyPointRecordException;
import com.nhnacademy.bookstore.member.pointRecord.exception.NotEnoughPointException;
import com.nhnacademy.bookstore.member.pointRecord.repository.PointRecordRepository;
import com.nhnacademy.bookstore.member.pointRecord.service.PointRecordService;
import com.nhnacademy.bookstore.purchase.purchase.exception.PurchaseDoesNotExistException;
import com.nhnacademy.bookstore.purchase.purchase.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 포인트 레코드 서비스 구현체.
 *
 * @author 김병우
 */
@RequiredArgsConstructor
@Service
@Transactional
public class PointRecordServiceImpl implements PointRecordService {
    private final MemberPointService memberPointService;
    private final PointRecordRepository pointRecordRepository;
    private final MemberRepository memberRepository;
    private final PurchaseRepository purchaseRepository;

    /**
     * Save point record. -point가 추가,사용 되었을때 레코드를 저장한다.
     * @author -유지아
     * fix - 김병우
     *
     * @param usePoint 사용, 적립 포인트
     * @param content 적립내역 사유
     * @param memberId 맴버 아이디
     * @param purchaseId 구매 아이디
     * @return 유저레코드아이디
     */
    public Long save(Long usePoint, String content, Long memberId, Long purchaseId) {
        Member member = memberRepository
                .findById(memberId)
                .orElseThrow(MemberNotExistsException::new);

        Purchase purchase = purchaseRepository
                .findById(purchaseId)
                .orElseThrow(()-> new PurchaseDoesNotExistException(purchaseId + "가 존재하지 않습니다"));

        if (member.getPoint() + usePoint < 0) {
           throw new NotEnoughPointException("현재 잔여 포인트 : " + member.getPoint() + "  -  잔여 포인트가 부족합니다.");
        }

        PointRecord pointRecord = new PointRecord(usePoint, "buy point", member, purchase);

        pointRecordRepository.save(pointRecord);
        memberPointService.updatePoint(memberId, usePoint);


        return pointRecord.getId();
    }

    /**
     * 포인트레코드 맴버 찾기.
     *
     * @param memberId 맴버아이디
     * @return 포인트레코드 dto 리스트
     */
    @Override
    public List<ReadPointRecordResponse> readByMemberId(Long memberId) {
        Member member = memberRepository
                .findById(memberId)
                .orElseThrow(MemberNotExistsException::new);

        List<PointRecord> pointRecords = pointRecordRepository.findAllByMember(member);

        return pointRecords.stream()
                .map(pointRecord -> ReadPointRecordResponse.builder()
                        .recordId(pointRecord.getId())
                        .usePoint(pointRecord.getUsePoint())
                        .createdAt(pointRecord.getCreatedAt())
                        .content(pointRecord.getContent())
                        .build())
                .toList();
    }

    /**
     * 포인트 환불시 사용 메소드
     *
     * @param purchaseId 주문아이디
     * @return 포인트레코드 아이디
     */
    @Override
    public Long refundByPurchaseId(Long purchaseId) {
        Purchase purchase = purchaseRepository
                .findById(purchaseId)
                .orElseThrow(()-> new PurchaseDoesNotExistException(purchaseId + "가 존재하지 않습니다"));

        List<PointRecord> pointRecords = pointRecordRepository.findAllByPurchase(purchase);

        for (PointRecord p : pointRecords) {
            if (p.getContent().equals("buy point")) {
                PointRecord pointRecord = new PointRecord(
                        -1 * p.getUsePoint(),
                        p.getContent() + "- point refund",
                        p.getMember(),
                        p.getPurchase()
                );

                pointRecordRepository.save(pointRecord);
                memberPointService.updatePoint(p.getMember().getId(), -1 * p.getUsePoint());

                return pointRecord.getId();
            }
        }

        throw new NoBuyPointRecordException("현재 포인트 이력이 없습니다.");
    }
}
