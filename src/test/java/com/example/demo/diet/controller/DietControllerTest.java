package com.example.demo.diet.controller;

import com.example.demo.domain.diet.dto.request.DietRecommendationRequest;
import com.example.demo.domain.diet.service.DietService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql({"/insert-state.sql", "/insert-dist.sql"})
class DietControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DietService dietService;

    private ObjectMapper objectMapper;


    @Test
    @DisplayName("GET /diet - 식단 목록 조회 성공")
    void getDietList_success() throws Exception {
        mockMvc.perform(get("/diet?order=asc"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /diet/info - 상세 조회 성공")
    void getDietDetail_success() throws Exception {
        mockMvc.perform(get("/diet/info?dietId=1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /diet/recommendations - 추천 생성 성공")
    void createDietRecommendation_success() throws Exception {
        DietRecommendationRequest req = new DietRecommendationRequest();
        req.setStateId(1L);
        req.setRequest("매운 음식 먹고 싶어");
        req.setRecommendedRange("morning");

        Mockito.when(dietService.createDietRecommendation(Mockito.any()))
                .thenReturn(
                        com.example.demo.domain.diet.dto.response.DietRecommendationResponse
                                .builder()
                                .message("ok")
                                .build()
                );

        mockMvc.perform(post("/diet/recommendations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }
}