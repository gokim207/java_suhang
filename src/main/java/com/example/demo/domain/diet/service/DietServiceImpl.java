package com.example.demo.domain.diet.service;

import com.example.demo.domain.diet.domain.Dist;
import com.example.demo.domain.diet.domain.enums.SortOrder;
import com.example.demo.global.exception.DietNotFoundException;
import com.example.demo.global.exception.DietServerException;
import com.example.demo.global.exception.InvalidDietRequestException;
import com.example.demo.domain.diet.dto.request.DietDetailRequest;
import com.example.demo.domain.diet.dto.request.DietListRequest;
import com.example.demo.domain.diet.dto.request.DietRecommendationRequest;
import com.example.demo.domain.diet.dto.response.DietDetailResponse;
import com.example.demo.domain.diet.dto.response.DietListItem;
import com.example.demo.domain.diet.dto.response.DietListResponse;
import com.example.demo.domain.diet.dto.response.DietRecommendationResponse;
import com.example.demo.domain.diet.repository.DietJpaRepo;
import com.example.demo.domain.state.domain.State;
import com.example.demo.domain.state.repository.StateJpaRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DietServiceImpl implements DietService {

    private final DietJpaRepo dietJpaRepo;
    private final StateJpaRepo stateJpaRepo;
    private final GeminiService geminiAIService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 메인화면 - 유저의 식단 조회
     */
    @Override
    public DietListResponse getDietList(DietListRequest request) {
        try {
            SortOrder sortOrder = validateAndParseSortOrder(request.getOrder());

            List<Dist> diets = sortOrder == SortOrder.ASC
                    ? dietJpaRepo.findAllByOrderByCreatedAtAsc()
                    : dietJpaRepo.findAllByOrderByCreatedAtDesc();

            List<DietListItem> dietListItems = diets.stream()
                    .map(this::convertToDietListItem)
                    .collect(Collectors.toList());

            return DietListResponse.builder()
                    .diets(dietListItems)
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Invalid order parameter: {}", request.getOrder(), e);
            throw new InvalidDietRequestException("order가 정상적으로 입력되지 않았습니다");
        } catch (Exception e) {
            log.error("Failed to retrieve diet list", e);
            throw new DietServerException("서버 내부 오류로 인하여 메인상태 변경에 실패하였습니다.");
        }
    }

    /**
     * 메인화면 - 식단정보 상세조회
     */
    @Override
    public DietDetailResponse getDietDetail(DietDetailRequest request) {
        try {
            if (request.getDietId() == null) {
                throw new InvalidDietRequestException("id가 정상적으로 입력되지 않았습니다");
            }

            Dist diet = dietJpaRepo.findByDietId(request.getDietId())
                    .orElseThrow(() -> new DietNotFoundException(request.getDietId()));

            return convertToDietDetailResponse(diet);

        } catch (DietNotFoundException | InvalidDietRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to retrieve diet detail for dietId: {}", request.getDietId(), e);
            throw new DietServerException("서버 내부 오류로 인하여 조회에 실패하였습니다.");
        }
    }

    /**
     * 식단 추천 생성 (아직 State 부분에 개발되지 않아 오류 있음)
     */
    @Override
    @Transactional
    public DietRecommendationResponse createDietRecommendation(DietRecommendationRequest request) {
        try {
            validateDietRecommendationRequest(request);

            // 1. State 정보 조회
            State state = stateJpaRepo.findById(request.getStateId())
                    .orElseThrow(() -> new InvalidDietRequestException("상태 정보를 찾을 수 없습니다."));


            log.info("State 조회 완료 - name: {}, description: {}",
                    state.getName(), state.getDescription());

            // TODO: User 정보 조회 (현재는 임시 값 사용)
            String userFoodCategories = "한식, 양식";
            String userFoodTypes = "담백한 음식";
            String userGender = "남성";
            Integer userAge = 25;

            // 2. Gemini API 1단계: 식재료 추천
            String ingredientName = geminiAIService.getFoodName(
                    userFoodCategories,
                    userFoodTypes,
                    userGender,
                    userAge,
                    state.getName(),
                    state.getDescription(),
                    state.getStandard(),
                    request.getRequest(),
                    request.getRecommendedRange()
            );

            log.info("추천된 식재료: {}", ingredientName);

            // 3. Gemini API 2단계: 식단 추천
            String dietRecommendation = geminiAIService.getDietRecommendation(
                    userFoodCategories,
                    userFoodTypes,
                    userGender,
                    userAge,
                    state.getName(),
                    state.getDescription(),
                    state.getStandard(),
                    request.getRequest(),
                    request.getRecommendedRange(),
                    ingredientName
            );

            log.info("식단 추천 완료");

            // 4. DB 저장
            Dist diet = Dist.builder()
                    .dietStateName(state.getName())
                    .request(request.getRequest())
                    .content(dietRecommendation)
                    .dietName(request.getRecommendedRange())
                    .dietContent(ingredientName)
                    .build();

            dietJpaRepo.save(diet);

            // 5. 응답 반환
            return DietRecommendationResponse.builder()
                    .message(dietRecommendation)
                    .build();

        } catch (InvalidDietRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to create diet recommendation", e);

            if (e.getMessage() != null && e.getMessage().contains("AI")) {
                throw new DietServerException("ai 오류로 인하여 식단 추천에 실패하였습니다.");
            }

            throw new DietServerException("서버 내부 오류로 인하여 식단 추천에 실패하였습니다.");
        }
    }


    /**
     * 정렬 순서 문자열을 SortOrder Enum으로 변환
     * order가 null이거나 빈 값이면 기본값(DESC) 반환
     */
    private SortOrder validateAndParseSortOrder(String order) {
        if (order == null || order.trim().isEmpty()) {
            return SortOrder.DESC;
        }
        return SortOrder.fromCode(order);
    }

    /**
     * 식단 추천 요청 데이터 유효성 검증
     * stateId, request, recommendedRange 필수 값 체크
     */
    private void validateDietRecommendationRequest(DietRecommendationRequest request) {
        if (request.getStateId() == null ||
                request.getRequest() == null || request.getRequest().trim().isEmpty() ||
                request.getRecommendedRange() == null || request.getRecommendedRange().trim().isEmpty()) {
            throw new InvalidDietRequestException("생성에 필요한 값이 정상적으로 입력되지 않았습니다");
        }
    }

    /**
     * Dist 엔티티를 DietListItem DTO로 변환
     * 식단 목록 조회 응답용
     */
    private DietListItem convertToDietListItem(Dist diet) {
        return DietListItem.builder()
                .dietId(diet.getDietId())
                .createDate(diet.getCreatedAt().format(DATE_FORMATTER))
                .updatedDate(diet.getUpdatedAt().format(DATE_FORMATTER))
                .stateName(diet.getDietStateName())
                .request(diet.getRequest())
                .build();
    }

    /**
     * Dist 엔티티를 DietDetailResponse DTO로 변환
     * 식단 상세 조회 응답용
     */
    private DietDetailResponse convertToDietDetailResponse(Dist diet) {
        return DietDetailResponse.builder()
                .createDate(diet.getCreatedAt() != null
                        ? diet.getCreatedAt().format(DATE_FORMATTER)
                        : LocalDateTime.now().format(DATE_FORMATTER))
                .updatedDate(diet.getUpdatedAt() != null
                        ? diet.getUpdatedAt().format(DATE_FORMATTER)
                        : LocalDateTime.now().format(DATE_FORMATTER))
                .stateName(diet.getDietStateName())
                .request(diet.getRequest())
                .content(diet.getContent())
                .build();
    }


}