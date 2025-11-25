package com.example.demo.domain.diet.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 식단 추천 시간대
 */
@Getter
@RequiredArgsConstructor
public enum RecommendedRange {
    MORNING("morning", "아침"),
    LUNCH("lunch", "점심"),
    DINNER("dinner", "저녁"),
    ALL("all", "하루 전체");

    private final String code;
    private final String description;

    public static RecommendedRange fromCode(String code) {
        for (RecommendedRange range : values()) {
            if (range.getCode().equalsIgnoreCase(code)) {
                return range;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 추천 시간대입니다: " + code);
    }
}
