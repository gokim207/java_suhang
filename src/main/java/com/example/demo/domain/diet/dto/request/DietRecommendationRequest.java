package com.example.demo.domain.diet.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DietRecommendationRequest {
    private Long stateId;
    private String request;
    private String recommendedRange;
}