package com.example.demo.diet.service;

import com.example.demo.diet.dto.exception.DietAIException;
import dev.failsafe.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import dev.failsafe.function.CheckedSupplier;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Callable;

@Slf4j
@Service
public class GeminiService {

    private final String GEMINI_MODEL = "gemini-1.5-flash";
    private final String GEMINI_API_KEY;
    private final String GEMINI_API_URL;

    private final RestTemplate restTemplate = new RestTemplate();

    /** Failsafe 정책 */
    private final RetryPolicy<Object> retryPolicy;
    private final CircuitBreaker<Object> circuitBreaker;
    private final Timeout<Object> timeout;

    public GeminiService(@Value("${gemini.api-key}") String apiKey) {
        this.GEMINI_API_KEY = apiKey;
        this.GEMINI_API_URL =
                "https://generativelanguage.googleapis.com/v1beta/models/"
                        + GEMINI_MODEL + ":generateContent?key=" + GEMINI_API_KEY;

        // Retry: 최대 3번 재시도
        this.retryPolicy = RetryPolicy.builder()
                .handle(Exception.class)
                .withDelay(Duration.ofMillis(500))
                .withMaxRetries(3)
                .onRetry(e -> log.warn("재시도 중... {}", e.getLastException().getMessage()))
                .build();

        // CircuitBreaker: 5번 연속 실패 시 30초 오픈
        this.circuitBreaker = CircuitBreaker.builder()
                .handle(Exception.class)
                .withFailureThreshold(5, 5)
                .withSuccessThreshold(3)
                .withDelay(Duration.ofSeconds(30))
                .onOpen(event -> log.warn("CircuitBreaker OPEN"))
                .onClose(event -> log.info("CircuitBreaker CLOSE"))
                .build();

        // Timeout: 6초 (이벤트는 호출 시 처리)
        this.timeout = Timeout.of(Duration.ofSeconds(6));
    }

    /**
     * 1단계: 식재료 이름 추천
     */
    public String getFoodName(
            String userFoodCategories,
            String userFoodTypes,
            String userGender,
            Integer userAge,
            String userStateName,
            String userStateDescription,
            String userStateStandard,
            String additionalRequests,
            String dietRecommendationRange
    ) {
        log.info("getFoodName 호출");

        String prompt = String.format("""
            You are a nutrition and culinary AI assistant helping to generate a single key ingredient for a personalized meal recommendation.
            
            Use the following information about the user to determine one suitable core ingredient that best fits the user's meal plan.
            
            [User Information]
            - Preferred cuisine category: %s
            - Preferred food type: %s
            - Gender: %s
            - Age: %s
            
            [User Situation]
            - Situation name: %s
            - Situation description: %s
            - Additional situation info: %s
            
            [Form Information]
            - Additional requests: %s
            - Diet recommendation range: %s
            
            Your task:
            1. Recommend only one core ingredient.
            2. Output must be strictly in Korean.
            3. Only the ingredient name.
            """,
                userFoodCategories, userFoodTypes, userGender, userAge,
                userStateName, userStateDescription, userStateStandard,
                additionalRequests, dietRecommendationRange);

        return callGeminiAPI(prompt);
    }

    /**
     * 2단계: 식단 추천
     */
    public String getDietRecommendation(
            String userFoodCategories,
            String userFoodTypes,
            String userGender,
            Integer userAge,
            String userStateName,
            String userStateDescription,
            String userStateStandard,
            String additionalRequests,
            String dietRecommendationRange,
            String ingredientName
    ) {
        log.info("getDietRecommendation 호출 - ingredientName: {}", ingredientName);

        String prompt = String.format("""
            You are a professional nutritionist and AI chef assistant.
            Based on the provided user information and the main ingredient, create a realistic and appealing meal recommendation in Korean.
            
            Use the information below to generate a **Korean markdown-style meal suggestion** that includes:
            1. For each meal (아침, 점심, 저녁):
               - Recommend one main dish (메인 메뉴)
               - Recommend two side dishes (사이드 메뉴)
            2. If the user selects "전부", provide recommendations for all three meals (아침, 점심, 저녁).
            3. The reason for the recommendation (매뉴를 추천한 이유)
            4. Other related meal suggestions or alternatives (다른 음식 추천)
            
            [User Information]
            - Preferred cuisine category: %s
            - Preferred food type: %s
            - Gender: %s
            - Age: %s
            
            [User Situation]
            - Situation name: %s
            - Situation description: %s
            - Additional situation info: %s
            
            [Form Information]
            - Additional requests: %s
            - Diet recommendation range: %s
            
            [Main Ingredient]
            - %s
            
            Your output must:
            - Be written **in Korean**
            - Use **Markdown formatting**
            - Follow this exact structure:
            
            # 🍽️ 아침
            - 메인 메뉴: (추천하는 아침 메인 메뉴)
            - 사이드 메뉴 1: (추천하는 아침 사이드 메뉴 1)
            - 사이드 메뉴 2: (추천하는 아침 사이드 메뉴 2)
            
            ## 💡 매뉴를 추천한 이유
            (해당 메뉴를 추천한 이유를 자연스럽게 설명)
            
            ## 🥗 다른 음식 추천
            (예: "~을 먹으시니 ~도 좋아하실 것 같아요. 최근에는 ~가 인기로 알고 있는데 ~는 어떠신가요?" 형식으로 2~3줄 작성) (다른 음식 추천 이후 구분선 추가)
            
            Guidelines:
            - The recommendation must match the user's preferences, situation, and dietary range.
            - The menu name must be a real dish commonly eaten in Korea.
            - Avoid lists or numbering. Keep tone friendly, informative, and natural.
            """,
                userFoodCategories, userFoodTypes, userGender, userAge,
                userStateName, userStateDescription, userStateStandard,
                additionalRequests, dietRecommendationRange, ingredientName
        );

        return callGeminiAPI(prompt);
    }

    /**
     * Gemini API 호출
     * - Failsafe v3 기반: Retry + CircuitBreaker + Timeout 적용
     */
    private String callGeminiAPI(String prompt) {
        // 요청 Body
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, String> part = new HashMap<>();
        part.put("text", prompt);
        content.put("parts", List.of(part));
        requestBody.put("contents", List.of(content));

        // HTTP 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 핵심 API 호출
        CheckedSupplier<String> apiCall = () -> {
            ResponseEntity<Map> response = restTemplate.exchange(
                    GEMINI_API_URL,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            if (body == null) throw new DietAIException("Gemini API 응답이 null");

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
            if (candidates == null || candidates.isEmpty())
                throw new DietAIException("Gemini API candidates 비어 있음");

            Map<String, Object> first = candidates.get(0);
            Map<String, Object> contentMap = (Map<String, Object>) first.get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");

            return (String) parts.get(0).get("text");
        };

        try {
            // Failsafe 적용 (Timeout + CircuitBreaker + Retry)
            return Failsafe.with(timeout, circuitBreaker, retryPolicy)
                    .get(apiCall);  // 실패하면 예외 발생

        } catch (dev.failsafe.TimeoutExceededException e) {
            log.error("Timeout 발생", e);
            throw new DietAIException("Timeout 발생", e);

        } catch (Exception e) {
            log.error("Gemini API 호출 실패", e);
            throw new DietAIException("AI 호출 오류: " + e.getMessage(), e);
        }
    }
}