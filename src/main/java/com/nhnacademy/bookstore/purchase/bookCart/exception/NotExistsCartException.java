package com.nhnacademy.bookstore.purchase.bookCart.exception;

public class NotExistsCartException extends RuntimeException {
	public NotExistsCartException() {
		super("요청하신 cart가 없습니다.");
	}
}
