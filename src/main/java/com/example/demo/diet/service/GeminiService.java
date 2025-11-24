package com.example.demo.diet.service;

import com.example.demo.diet.dto.exception.DietAIException;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Google Gemini API를 사용한 식단 추천 서비스
 */
@Slf4j
@Service
public class GeminiService {

    private final String GEMINI_MODEL = "gemini-1.5-flash";
    private final String GEMINI_API_KEY;
    private final String GEMINI_API_URL;

    private final RestTemplate restTemplate = new RestTemplate();

    public GeminiService(@Value("${gemini.api-key}") String apiKey) {
        this.GEMINI_API_KEY = apiKey;
        this.GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/"
                + GEMINI_MODEL + ":generateContent?key=" + GEMINI_API_KEY;
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
            1. Recommend **only one core ingredient** that best matches the user's situation and preferences.
            2. The ingredient should be realistic and typically used in meals fitting the provided context.
            3. Output format **must be strictly in Korean**, containing only the ingredient name (no brackets, no extra words).
            4. Do not include any explanations, punctuation, or numbering — **return only the ingredient name**.
            """,
                userFoodCategories, userFoodTypes, userGender, userAge,
                userStateName, userStateDescription, userStateStandard,
                additionalRequests, dietRecommendationRange
        );

        return callGeminiAPI(prompt);
    }

    /**
     * 2단계: 식단 추천 (Markdown 형식)
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
     * Gemini API 호출 (REST API 방식)
     */
    private String callGeminiAPI(String prompt) {
        try {
            // 요청 본문 생성
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            Map<String, String> part = new HashMap<>();

            part.put("text", prompt);
            content.put("parts", List.of(part));
            requestBody.put("contents", List.of(content));

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // API 호출
            ResponseEntity<Map> response = restTemplate.exchange(
                    GEMINI_API_URL,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            // 응답 파싱
            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) {
                throw new DietAIException("Gemini API 응답이 비어있습니다.");
            }

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new DietAIException("Gemini API 응답에 candidates가 없습니다.");
            }

            Map<String, Object> firstCandidate = candidates.get(0);
            Map<String, Object> contentMap = (Map<String, Object>) firstCandidate.get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");
            String text = (String) parts.get(0).get("text");

            log.info("Gemini API 응답 성공: {}", text);
            return text;

        } catch (Exception e) {
            log.error("Gemini API 호출 실패", e);
            throw new DietAIException("AI 호출 중 오류 발생: " + e.getMessage());
        }
    }
}