package com.example.demo.diet.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DietListItem {
    private Long dietId;
    private String createDate;
    private String updatedDate;
    private String stateName;
    private String request;
}