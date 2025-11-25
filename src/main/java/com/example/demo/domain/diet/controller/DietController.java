package com.example.demo.domain.diet.controller;

import com.example.demo.domain.diet.dto.request.DietDetailRequest;
import com.example.demo.domain.diet.dto.request.DietListRequest;
import com.example.demo.domain.diet.dto.request.DietRecommendationRequest;
import com.example.demo.domain.diet.dto.response.DietDetailResponse;
import com.example.demo.domain.diet.dto.response.DietListResponse;
import com.example.demo.domain.diet.dto.response.DietRecommendationResponse;
import com.example.demo.domain.diet.service.DietService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 식단 관리 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/diet")
@RequiredArgsConstructor
public class DietController {

    private final DietService dietService;

    /**
     * 메인화면 - 유저의 식단 조회
     * GET /diet?order=asc
     */
    @GetMapping
    public ResponseEntity<DietListResponse> getDietList(
            @RequestParam(required = false, defaultValue = "desc") String order) {

        log.info("GET /diet - order: {}", order);

        DietListRequest request = new DietListRequest();
        request.setOrder(order);

        DietListResponse response = dietService.getDietList(request);

        return ResponseEntity.ok(response);
    }

    /**
     * 메인화면 - 식단정보 상세조회
     * GET /diet/info?dietId=2
     */
    @GetMapping("/info")
    public ResponseEntity<DietDetailResponse> getDietDetail(
            @RequestParam Long dietId) {

        log.info("GET /diet/info - dietId: {}", dietId);

        DietDetailRequest request = new DietDetailRequest();
        request.setDietId(dietId);

        DietDetailResponse response = dietService.getDietDetail(request);

        return ResponseEntity.ok(response);
    }

    /**
     * 식단 추천 받기 - 식단 추천
     * POST /diet/recommendations
     * Body: { "stateId": 2, "request": "오늘은 매운 음식을 위주로 먹고 싶어", "recommendedRange": "morning" }
     */
    @PostMapping("/recommendations")
    public ResponseEntity<DietRecommendationResponse> createDietRecommendation(
            @RequestBody DietRecommendationRequest request) {

        log.info("POST /diet/recommendations - stateId: {}, request: {}, range: {}",
                request.getStateId(), request.getRequest(), request.getRecommendedRange());

        DietRecommendationResponse response = dietService.createDietRecommendation(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}