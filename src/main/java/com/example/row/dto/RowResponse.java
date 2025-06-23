package com.example.row.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class RowResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDate targetDate;
    private LocalDateTime createdAt;
    private Integer dDay;  // 오늘 기준 남은 날짜 (계산된 값)
    private String content;
  
    // 기본 생성자
    public RowResponse() {}
}
