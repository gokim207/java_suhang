package com.example.demo.diet.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 정렬 순서
 */
@Getter
@RequiredArgsConstructor
public enum SortOrder {
    ASC("asc", "오름차순"),
    DESC("desc", "내림차순");

    private final String code;
    private final String description;

    public static SortOrder fromCode(String code) {
        for (SortOrder order : values()) {
            if (order.getCode().equalsIgnoreCase(code)) {
                return order;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 정렬 순서입니다: " + code);
    }
}