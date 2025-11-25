package com.example.demo.global.exception;

/**
 * 서버 내부 오류
 */
public class DietServerException extends RuntimeException {
    public DietServerException(String message) {
        super(message);
    }

    public DietServerException(String message, Throwable cause) {
        super(message, cause);
    }
}