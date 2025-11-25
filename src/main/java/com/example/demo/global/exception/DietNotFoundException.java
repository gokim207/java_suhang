package com.example.demo.global.exception;

/**
 * 식단을 찾을 수 없을 때 발생하는 예외
 */
public class DietNotFoundException extends RuntimeException {
    public DietNotFoundException(Long dietId) {
        super("id가 정상적으로 입력되지 않았습니다");
    }
}