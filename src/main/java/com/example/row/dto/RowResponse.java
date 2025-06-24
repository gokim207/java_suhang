package com.example.row.dto;

import com.example.row.entity.Row;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@AllArgsConstructor
public class RowResponse {
    private Long id;
    private String name;
    private String content;
    private LocalDate targetDate;
    private LocalDateTime createdAt;
    private boolean isCompleted = false;
    private Integer dDay;  // 오늘 기준 남은 날짜 (계산된 값)

    public RowResponse(Row row) {
        this.id = row.getId();
        this.name = row.getName();
        this.content = row.getContent();
        this.targetDate = row.getTargetDate();
        this.createdAt = row.getCreatedAt();
        this.isCompleted = row.isCompleted();
        this.dDay = (int) ChronoUnit.DAYS.between(LocalDate.now(), row.getTargetDate());
    }

    public void setContent(String content) {}
}
