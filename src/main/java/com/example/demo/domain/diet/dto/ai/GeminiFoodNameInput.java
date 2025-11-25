package com.example.demo.domain.diet.dto.ai;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class GeminiFoodNameInput {
    private String userFoodCategories;
    private String userFoodTypes;
    private String userGender;
    private Integer userAge;
    private String userStateName;
    private String userStateDescription;
    private String userStateInfo;
    private String additionalRequests;
    private String dietRecommendationRange;
}