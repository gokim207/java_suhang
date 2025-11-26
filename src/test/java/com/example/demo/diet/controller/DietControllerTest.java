package com.example.demo.diet.controller;

import com.example.demo.domain.diet.domain.Dist;
import com.example.demo.domain.diet.dto.request.DietRecommendationRequest;
import com.example.demo.domain.diet.repository.DietJpaRepo;
import com.example.demo.domain.diet.service.GeminiService;
import com.example.demo.domain.state.domain.State;
import com.example.demo.domain.state.repository.StateJpaRepo;
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
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql({"/insert-state.sql", "/insert-diet.sql"})
class DietControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // DietService Mock 제거 - 실제 Service 로직 사용
    // GeminiService만 Mock 처리
    @MockitoBean
    private GeminiService geminiService;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StateJpaRepo stateJpaRepo;
    @Autowired
    private DietJpaRepo dietJpaRepo;

    @Test
    @DisplayName("식단 목록 조회 성공")
    void getDietList_success() throws Exception {
        mockMvc.perform(get("/diet?order=asc"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("식단 목록 조회 실패 - 잘못된 정렬 값으로 400 반환")
    void getDietList_failure() throws Exception {
        mockMvc.perform(get("/diet?order=ffffff"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("상세 조회 성공")
    void getDietDetail_success() throws Exception {
        mockMvc.perform(get("/diet/info?dietId=1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("상세 조회 실패 - 존재하지 않는 dietId")
    void getDietDetail_failure() throws Exception {
        mockMvc.perform(get("/diet/info?dietId=100"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("추천 생성 성공")
    void createDietRecommendation_success() throws Exception {
        List<State> states = stateJpaRepo.findAll();
        assertThat(states).isNotEmpty();
        Long actualStateId = states.get(0).getId();

        System.out.println("사용할 State ID: " + actualStateId);

        // GeminiService Mock 설정
        Mockito.when(geminiService.getFoodName(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString()
        )).thenReturn("닭가슴살");

        Mockito.when(geminiService.getDietRecommendation(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString()
        )).thenReturn("추천 식단: 닭가슴살 샐러드");

        DietRecommendationRequest req = new DietRecommendationRequest();
        req.setStateId(actualStateId);
        req.setRequest("매운 음식 먹고 싶어");
        req.setRecommendedRange("morning");

        mockMvc.perform(post("/diet/recommendations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("추천 생성 실패 - 존재하지 않는 State ID")
    void createDietRecommendation_failure() throws Exception {
        // GeminiService Mock 설정 - 외부 API 호출 방지
        Mockito.when(geminiService.getFoodName(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString()
        )).thenReturn("닭가슴살");

        Mockito.when(geminiService.getDietRecommendation(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString()
        )).thenReturn("추천 식단: 닭가슴살 샐러드");


        DietRecommendationRequest req = new DietRecommendationRequest();
        req.setStateId(8L);
        req.setRequest("매운 음식 먹고 싶어");
        req.setRecommendedRange("morning");


        mockMvc.perform(post("/diet/recommendations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("상태 정보를 찾을 수 없습니다."));

    }
}