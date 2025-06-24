package com.example.row.entity;

import com.example.row.dto.RowRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Setter
@Table(name = "tb_row")
public class Row {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String content;
    private LocalDate targetDate;

    @CreatedDate
    private LocalDateTime createdAt;
    private String recurrence = "NONE";

    public Row() {}

    public Row(RowRequest dto) {
        this.name = dto.getName();
        this.content = dto.getContent();
        this.recurrence = dto.getRecurrence();
        if (dto.getTargetDate() != null) {
          String dateStr = String.valueOf(dto.getTargetDate());
            if (dateStr.length() == 10) { // "yyyy-MM-dd" 형식
                this.targetDate = LocalDate.from(LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE).atStartOfDay());
                    } else {
                this.targetDate = LocalDate.from(LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME));
            }
        }
        this.createdAt = LocalDateTime.now();
    }

    public void updateFromDto(RowRequest dto) {
        this.name = dto.getName();
        this.content = dto.getContent();
        this.recurrence = dto.getRecurrence();
        if (dto.getTargetDate() != null) {
            String dateStr = String.valueOf(dto.getTargetDate());
            if (dateStr.length() == 10) { // "yyyy-MM-dd"
                this.targetDate = LocalDate.from(LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE).atStartOfDay());
            } else {
                this.targetDate = LocalDate.from(LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME));
            }
        }
    }
}
