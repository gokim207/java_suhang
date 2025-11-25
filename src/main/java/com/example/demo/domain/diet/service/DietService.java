package com.example.demo.domain.diet.service;


import com.example.demo.domain.diet.dto.request.DietDetailRequest;
import com.example.demo.domain.diet.dto.request.DietListRequest;
import com.example.demo.domain.diet.dto.request.DietRecommendationRequest;
import com.example.demo.domain.diet.dto.response.DietDetailResponse;
import com.example.demo.domain.diet.dto.response.DietListResponse;
import com.example.demo.domain.diet.dto.response.DietRecommendationResponse;

public interface DietService {

    DietListResponse getDietList(DietListRequest request);

    DietDetailResponse getDietDetail(DietDetailRequest request);

    DietRecommendationResponse createDietRecommendation(DietRecommendationRequest request);
}