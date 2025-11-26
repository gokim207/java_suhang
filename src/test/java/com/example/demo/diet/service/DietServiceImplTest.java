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
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
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
    @DisplayName("식단 목록 조회 실패 - 잘못된 order 값")
    void getDietList_failure() {
        // given
        DietListRequest request = new DietListRequest();
        request.setOrder("실패를 부탁해요");

        // when & then
        assertThatThrownBy(() -> dietService.getDietList(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("올바르지 않은 order 값입니다.");
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
    @DisplayName("getFoodName 호출 테스트 성공")
    void getFoodName_success() {
        // given
        String expectedIngredient = "Recommended Ingredient";

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
        )).thenReturn(expectedIngredient);

        // when
        String ingredient = geminiService.getFoodName(
                "한식", "밥", "남", 25,
                "기분 좋음", "담백한 음식", "표준", "추가 요청", "morning"
        );

        // then
        assertThat(ingredient)
                .as("AI가 추천한 식재료 확인")
                .isEqualTo(expectedIngredient);
    }

    @Test
    @DisplayName("getDietRecommendation 호출 테스트 성공")
    void getDietRecommendation_success() {
        // given
        String expectedRecommendation = "Diet Recommendation Result";

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
        )).thenReturn(expectedRecommendation);

        // when
        String recommendation = geminiService.getDietRecommendation(
                "한식", "밥", "남", 25,
                "기분 좋음", "담백한 음식", "표준", "추가 요청", "morning",
                "Recommended Ingredient"
        );

        // then
        assertThat(recommendation)
                .as("AI가 추천한 식단 메시지 확인")
                .isEqualTo(expectedRecommendation);
    }


}