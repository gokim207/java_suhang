package com.example.row.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RowRequest {
    private String name;
    private String content;
    private String targetDate;
    private String recurrence = "NONE";
}
