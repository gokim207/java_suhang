package com.example.demo.diet.service;

import com.example.demo.diet.domain.Dist;
import com.example.demo.diet.domain.enums.SortOrder;
import com.example.demo.diet.dto.exception.DietNotFoundException;
import com.example.demo.diet.dto.exception.InvalidDietRequestException;
import com.example.demo.diet.dto.request.DietDetailRequest;
import com.example.demo.diet.dto.request.DietListRequest;
import com.example.demo.diet.dto.request.DietRecommendationRequest;
import com.example.demo.diet.dto.response.DietDetailResponse;
import com.example.demo.diet.dto.response.DietListResponse;
import com.example.demo.diet.dto.response.DietRecommendationResponse;
import com.example.demo.diet.repository.DietJpaRepo;
import com.example.demo.state.domain.State;
import com.example.demo.state.repository.StateJpaRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DietServiceImplTest {

    @Mock
    private DietJpaRepo dietJpaRepo;

    @Mock
    private StateJpaRepo stateJpaRepo;

    @Mock
    private GeminiService geminiAIService;

    @InjectMocks
    private DietServiceImpl dietService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Sql("/insert-dist.sql")
    @Test
    void getDietList_ReturnsDietList() {

        List<Dist> result =  dietJpaRepo.findAllByOrderByCreatedAtDesc();

        DietListRequest request = new DietListRequest();
        request.setOrder("DESC");

        DietListResponse response = dietService.getDietList(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void getDietDetail_ValidId_ReturnsDietDetail() {

    }

    @Test
    void getDietDetail_InvalidId_ThrowsException() {

    }

    @Test
    void createDietRecommendation_ValidRequest_ReturnsRecommendation() {
        State state = State.builder()
                .id(1L)
                .name("다이어트중")
                .description("체중 감량 필요")
                .standard("표준")
                .build();

        when(stateJpaRepo.findById(1L)).thenReturn(Optional.of(state));
        when(geminiAIService.getFoodName(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn("닭가슴살");
        when(geminiAIService.getDietRecommendation(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn("닭가슴살 + 샐러드");

        DietRecommendationRequest request = new DietRecommendationRequest();
        request.setStateId(1L);
        request.setRequest("체중 감량");
        request.setRecommendedRange("1500kcal");

        DietRecommendationResponse response = dietService.createDietRecommendation(request);

        assertNotNull(response);
        assertEquals("닭가슴살 + 샐러드", response.getMessage());
        verify(dietJpaRepo, times(1)).save(any(Dist.class));
    }

    @Test
    void createDietRecommendation_MissingStateId_ThrowsException() {
        DietRecommendationRequest request = new DietRecommendationRequest();
        request.setRequest("체중 감량");
        request.setRecommendedRange("1500kcal");

        assertThrows(InvalidDietRequestException.class, () -> dietService.createDietRecommendation(request));
    }
}
