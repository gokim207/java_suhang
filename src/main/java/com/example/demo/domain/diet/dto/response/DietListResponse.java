package com.example.demo.domain.diet.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DietListResponse {
    private List<DietListItem> diets;
}