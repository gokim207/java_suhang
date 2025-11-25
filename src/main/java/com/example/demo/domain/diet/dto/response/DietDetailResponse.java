package com.example.demo.domain.diet.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DietDetailResponse {
    private String createDate;
    private String updatedDate;
    private String stateName;
    private String request;
    private String content;
}
