package com.nhnacademy.bookstore.member.pointRecord.service;

import com.nhnacademy.bookstore.entity.pointRecord.PointRecord;
import com.nhnacademy.bookstore.member.pointRecord.dto.response.ReadPointRecordResponse;

import java.util.List;

/**
 * 포인트 서비스 인터페이스.
 *
 * @author 김병우
 */
public interface PointRecordService {
    Long save(Long usePoint, String content, Long memberId, Long purchaseId);

    Long refundByPurchaseId(Long purchaseId);

    List<ReadPointRecordResponse> readByMemberId(Long memberId);

}
