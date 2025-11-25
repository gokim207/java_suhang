package com.example.demo.global.exception;

/**
 * AI 서비스 오류
 */
public class DietAIException extends RuntimeException {
    public DietAIException(String message) {
        super("ai 오류로 인하여 식단 추천에 실패하였습니다.");
    }

    public DietAIException(String message, Throwable cause) {
        super("ai 오류로 인하여 식단 추천에 실패하였습니다.", cause);
    }
}