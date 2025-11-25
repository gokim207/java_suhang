package com.example.demo.global.exception;

/**
 * 유효하지 않은 요청값일 때 발생하는 예외
 */
public class InvalidDietRequestException extends RuntimeException {
    public InvalidDietRequestException(String message) {
        super(message);
    }
}
