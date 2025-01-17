package com.nhnacademy.bookstore.purchase.bookCart.dto.response;

import lombok.*;

/**
 * 북카트 비회원 읽기 응답.
 *
 * @param bookCartId
 * @param bookId
 * @param price
 * @param url
 * @param title
 * @param quantity
 * @param leftQuantity
 */
@Builder
public record ReadBookCartGuestResponse(
        Long bookCartId,
        Long bookId,
        int price,
        String url,
        String title,
        int quantity,
        int leftQuantity){
}
