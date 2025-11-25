package com.example.demo.diet.controller;

import com.example.demo.diet.dto.request.DietRecommendationRequest;
import com.example.demo.diet.dto.response.DietDetailResponse;
import com.example.demo.diet.dto.response.DietListResponse;
import com.example.demo.diet.dto.response.DietRecommendationResponse;
import com.example.demo.diet.service.DietService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DietControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DietService dietService;

    @Autowired
    private ObjectMapper objectMapper;

    private DietListResponse dummyListResponse;
    private DietDetailResponse dummyDetailResponse;
    private DietRecommendationResponse dummyRecommendationResponse;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Sql("/insert-dist.sql")
    @Test
    void getDietList() throws Exception {
        ResultActions result = mockMvc.perform(get("/diet")
        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dummyListResponse)));

    }

    @Sql("/insert-dist.sql")
    @Test
    void getDietDetail() throws Exception {
        mockMvc.perform(get("/diet/info")
                        .param("dietId", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Sql("/insert-state.sql")
    @Test
    void createDietRecommendation() throws Exception {
        DietRecommendationRequest request = new DietRecommendationRequest();
        request.setStateId(2L);
        request.setRequest("오늘은 매운 음식을 위주로 먹고 싶어");
        request.setRecommendedRange("morning");

        mockMvc.perform(post("/diet/recommendations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}