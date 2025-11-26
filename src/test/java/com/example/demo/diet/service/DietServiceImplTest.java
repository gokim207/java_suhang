package com.example.demo.diet.service;

import com.example.demo.domain.diet.dto.request.DietRecommendationRequest;
import com.example.demo.domain.diet.repository.DietJpaRepo;
import com.example.demo.domain.state.repository.StateJpaRepo;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("/insert-dist.sql")
class DietServiceImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DietJpaRepo dietJpaRepo;

    @Autowired
    private StateJpaRepo stateJpaRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;
    @Test
    @DisplayName("식단 목록 조회 - 성공")
    void getDietList_Success() throws Exception {

        mockMvc.perform(get("/diet/list")
                        .param("order", "DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dietList").isArray())
                .andExpect(jsonPath("$.dietList.length()").value(dietJpaRepo.findAll().size()));
    }

    @Test
    @DisplayName("식단 추천 생성 - 성공")
    void createDietRecommendation_Success() throws Exception {

        DietRecommendationRequest request = new DietRecommendationRequest();
        request.setStateId(1L);
        request.setRequest("체중 감량");
        request.setRecommendedRange("1500kcal");

        mockMvc.perform(post("/diet/recommend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        assertThat(dietJpaRepo.findAll()).isNotEmpty();
    }

    @Test
    @DisplayName("식단 추천 생성 - stateId 누락으로 실패")
    void createDietRecommendation_Fail_MissingStateId() throws Exception {

        DietRecommendationRequest request = new DietRecommendationRequest();
        request.setRequest("체중 감량");
        request.setRecommendedRange("1500kcal");

        mockMvc.perform(post("/diet/recommend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("stateId가 존재하지 않습니다."));
    }
}