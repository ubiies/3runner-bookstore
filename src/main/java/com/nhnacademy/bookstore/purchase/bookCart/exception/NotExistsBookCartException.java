package com.nhnacademy.bookstore.purchase.bookCart.exception;

public class NotExistsBookCartException extends RuntimeException {
	public NotExistsBookCartException() {
		super("북-카트가 존재하지않습니다.");
	}
}
