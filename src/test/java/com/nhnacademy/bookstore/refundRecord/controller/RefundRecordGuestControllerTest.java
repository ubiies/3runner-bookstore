package com.nhnacademy.bookstore.refundRecord.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.nhnacademy.bookstore.BaseDocumentTest;
import com.nhnacademy.bookstore.purchase.purchaseBook.dto.response.ReadBookByPurchase;
import com.nhnacademy.bookstore.purchase.refundRecord.controller.RefundRecordGuestController;
import com.nhnacademy.bookstore.purchase.refundRecord.dto.request.CreateRefundRecordRedisRequest;
import com.nhnacademy.bookstore.purchase.refundRecord.service.RefundRecordGuestService;

@WebMvcTest(RefundRecordGuestController.class)
public class RefundRecordGuestControllerTest extends BaseDocumentTest {

	@MockBean
	private RefundRecordGuestService refundRecordGuestService;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void setUp(WebApplicationContext context) {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testCreateRefundRecordGuestRedis() throws Exception {
		CreateRefundRecordRedisRequest request =  CreateRefundRecordRedisRequest.builder().quantity(2).price(1000).readBookByPurchase(
			ReadBookByPurchase.builder().build()).build();
		when(refundRecordGuestService.createRefundRecordRedis(anyString(), anyLong(), anyInt(), anyInt(), any())).thenReturn(1L);

		mockMvc.perform(post("/bookstore/refundRecord/guests/{orderNumber}/{purchaseBookId}", "order123", 1L)
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(request))
				.accept("application/json"))
			.andExpect(status().isOk())
			.andDo(document("환불 내역 redis 생성(비회원)",
				pathParameters(
					parameterWithName("orderNumber").description("주문 Order-number"),
					parameterWithName("purchaseBookId").description("주문-책 id")
				),
				requestFields(
					fieldWithPath("price").type(JsonFieldType.NUMBER).description("환불 가격"),
					fieldWithPath("quantity").type(JsonFieldType.NUMBER).description("환불 수량"),
					fieldWithPath("readBookByPurchase.title").type(JsonFieldType.STRING).description("환불 할 책 제목").optional(),
					fieldWithPath("readBookByPurchase.price").type(JsonFieldType.NUMBER).description("환불 할 책 가격").optional(),
					fieldWithPath("readBookByPurchase.author").type(JsonFieldType.STRING).description("환불 할 책 저자").optional(),
					fieldWithPath("readBookByPurchase.packing").type(JsonFieldType.BOOLEAN).description("환불 할 책 포장여부").optional(),
					fieldWithPath("readBookByPurchase.publisher").type(JsonFieldType.STRING).description("환불 할 책 출판사").optional(),
					fieldWithPath("readBookByPurchase.bookImage").type(JsonFieldType.STRING).description("환불 할 책 사진").optional(),
					fieldWithPath("readBookByPurchase.sellingPrice").type(JsonFieldType.NUMBER).description("환불 할 책 판매가").optional()

				),
				responseFields(
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
					fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("생성된 환불 key")
				)
			));
	}

	@Test
	void testUpdateRefundRecordGuest() throws Exception {
		when(refundRecordGuestService.updateRefundRecordRedis(anyString(), anyLong(), anyInt())).thenReturn(1L);

		mockMvc.perform(put("/bookstore/refundRecord/guests/{orderNumber}/{purchaseBookId}", "order123", 1L)
				.queryParam("quantity", "3")
				.accept("application/json"))
			.andExpect(status().isOk())
			.andDo(document("환불 내역 수정 Redis(비회원)",
				pathParameters(
					parameterWithName("orderNumber").description("주문 order-number"),
					parameterWithName("purchaseBookId").description("주문-책 id")
				),
				queryParameters(
					parameterWithName("quantity").description("환불 수정 책 수량")
				),
				responseFields(
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
					fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("수정된 환불 key")
				)
			));
	}

	@Test
	void testDeleteRefundRecordGuest() throws Exception {
		when(refundRecordGuestService.deleteRefundRecordRedis(anyString(), anyLong())).thenReturn(1L);

		mockMvc.perform(delete("/bookstore/refundRecord/guests/{orderNumber}/{purchaseBookId}", "order123", 1L)
				.accept("application/json"))
			.andExpect(status().isOk())
			.andDo(document("비회원 환불 기록 제거",
				pathParameters(
					parameterWithName("orderNumber").description("삭제할 주문 Order-number"),
					parameterWithName("purchaseBookId").description("삭제할 주문-책 id")
				),
				responseFields(
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
					fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("삭제된 환불 key")
				)
			));
	}

	@Test
	void testCreateRefundRecordGuest() throws Exception {
		when(refundRecordGuestService.createRefundRecord(anyString(), anyLong())).thenReturn(true);

		mockMvc.perform(post("/bookstore/refundRecord/guests/save/{orderNumber}/{refundId}", "order123", 1L)
				.accept("application/json"))
			.andExpect(status().isOk())
			.andDo(document("create-refund-record-guest",
				pathParameters(
					parameterWithName("orderNumber").description("주문 order-number"),
					parameterWithName("refundId").description("환불 내역에 저장될 환불 ID")
				),
				responseFields(
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
					fieldWithPath("body.data").type(JsonFieldType.BOOLEAN).description("생성 성공 여부")
				)
			));
	}

	@Test
	void testUpdateRefundRecordAllMember() throws Exception {
		when(refundRecordGuestService.updateRefundRecordAllRedis(anyString())).thenReturn(true);

		mockMvc.perform(put("/bookstore/refundRecord/guests/all/{orderNumber}", "order123")
				.accept("application/json"))
			.andExpect(status().isOk())

			.andDo(document("모든 주문-환불 속 환불 내역 수정(최댓값)",
				pathParameters(
					parameterWithName("orderNumber").description("주문 order-number")
				),
				responseFields(
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
					fieldWithPath("body.data").type(JsonFieldType.BOOLEAN).description("수정 성공 여부")
				)
			));
	}

	@Test
	void testUpdateRefundRecordAllZeroMember() throws Exception {
		when(refundRecordGuestService.updateRefundRecordZeroAllRedis(anyString())).thenReturn(true);

		mockMvc.perform(put("/bookstore/refundRecord/guests/all/zero/{orderNumber}", "order123")
				.accept("application/json"))
			.andExpect(status().isOk())

			.andDo(document("모든 주문-환불 속 환불 내역 수정(0)",
				pathParameters(
					parameterWithName("orderNumber").description("주문 order-number")
				),
				responseFields(
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
					fieldWithPath("body.data").type(JsonFieldType.BOOLEAN).description("수정 성공 여부")
				)
			));
	}
}
