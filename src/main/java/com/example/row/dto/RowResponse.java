package com.example.row.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class RowResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDate targetDate;
    private LocalDateTime createdAt;
    private Integer dDay;  // 오늘 기준 남은 날짜 (계산된 값)

    public RowResponse() {}

    public void setContent(String content) {}
}
