//뭐가 어디 표시 하는지

package com.example.demo.diet.service;

import com.example.demo.domain.diet.dto.request.DietDetailRequest;
import com.example.demo.domain.diet.dto.request.DietListRequest;
import com.example.demo.domain.diet.dto.request.DietRecommendationRequest;
import com.example.demo.domain.diet.dto.response.DietDetailResponse;
import com.example.demo.domain.diet.dto.response.DietListResponse;
import com.example.demo.domain.diet.repository.DietJpaRepo;
import com.example.demo.domain.diet.service.DietService;
import com.example.demo.domain.diet.service.GeminiService;
import com.example.demo.domain.state.domain.State;
import com.example.demo.domain.state.repository.StateJpaRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Sql({"/insert-state.sql", "/insert-diet.sql"})
class DietServiceImplTest {

    @Autowired
    private DietService dietService;

    @Autowired
    private DietJpaRepo dietJpaRepo;

    @Autowired
    private StateJpaRepo stateJpaRepo;

    @MockitoBean
    private GeminiService geminiService;

    @Test
    @DisplayName("식단 목록 조회 성공")
    void getDietList_success() {
        // given
        DietListRequest request = new DietListRequest();
        request.setOrder("asc");

        // when
        DietListResponse response = dietService.getDietList(request);

        // then
        assertThat(response.getDiets()).isNotEmpty();
        assertThat(response.getDiets().size()).isEqualTo(5);
    }

    @Test
    @DisplayName("식단 상세 조회 성공")
    void getDietDetail_success() {
        // given
        Long sampleDietId = dietJpaRepo.findAll().get(0).getDietId();

        DietDetailRequest request = new DietDetailRequest();
        request.setDietId(sampleDietId);

        // when
        DietDetailResponse response = dietService.getDietDetail(request);

        // then
        assertThat(response.getContent()).isNotNull();
        assertThat(response.getStateName()).isNotNull();
    }

    @Test
    @DisplayName("식단 상세 조회 실패 - 존재하지 않는 dietId")
    void getDietDetail_notFound() {
        // given
        DietDetailRequest req = new DietDetailRequest();
        req.setDietId(9999L);

        // when & then
        assertThatThrownBy(() -> dietService.getDietDetail(req))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("식단 추천 생성 성공")
    void createDietRecommendation_success() {
        // given - insert-state.sql 에 이미 state 정보 있음
        State state = stateJpaRepo.findAll().get(0);

        DietRecommendationRequest req = new DietRecommendationRequest();
        req.setStateId(state.getId());
        req.setRequest("오늘은 담백한 음식이 먹고 싶어");
        req.setRecommendedRange("morning");

        // Gemini Mocking (AI 응답 고정)
        Mockito.when(geminiService.getFoodName(
                Mockito.anyString(),     // userFoodCategories
                Mockito.anyString(),     // userFoodTypes
                Mockito.anyString(),     // userGender
                Mockito.anyInt(),        // userAge
                Mockito.anyString(),     // userStateName
                Mockito.anyString(),     // userStateDescription
                Mockito.anyString(),     // userStateStandard
                Mockito.anyString(),     // additionalRequests
                Mockito.anyString()      // dietRecommendationRange
        )).thenReturn("Recommended Ingredient");

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
        )).thenReturn("Diet Recommendation Result");

        // when
        var res = dietService.createDietRecommendation(req);

        assertThat(res.getMessage())
                .as("AI 추천 메시지 확인")
                .isEqualTo("Diet Recommendation Result");
    }
}