package com.example.row.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RowRequest {
    @Setter
    @Getter
    @Schema(hidden = true)
    private Long id;

    private String name;
    private String content;
    private LocalDate targetDate;
    private boolean isCompleted = false;


    @Schema(hidden = true)
     private LocalDateTime createdAt;
}
