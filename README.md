
```
java_suhang
├─ .DS_Store
├─ gradle
│  └─ wrapper
│     ├─ gradle-wrapper.jar
│     └─ gradle-wrapper.properties
├─ gradlew
├─ gradlew.bat
└─ src
   ├─ .DS_Store
   ├─ main
   │  ├─ .DS_Store
   │  ├─ java
   │  │  └─ com
   │  │     └─ example
   │  │        └─ demo
   │  │           ├─ DemoApplication.java
   │  │           ├─ diet
   │  │           │  ├─ config
   │  │           │  │  └─ JpaConfig.java
   │  │           │  ├─ controller
   │  │           │  │  ├─ DietController.java
   │  │           │  │  └─ DietExceptionHandler.java
   │  │           │  ├─ domain
   │  │           │  │  ├─ BaseTimeEntity.java
   │  │           │  │  ├─ Dist.java
   │  │           │  │  └─ enums
   │  │           │  │     ├─ RecommendedRange.java
   │  │           │  │     └─ SortOrder.java
   │  │           │  ├─ dto
   │  │           │  │  ├─ ai
   │  │           │  │  │  ├─ GeminiDietRecommendationInput.java
   │  │           │  │  │  └─ GeminiFoodNameInput.java
   │  │           │  │  ├─ common
   │  │           │  │  │  └─ ErrorResponse.java
   │  │           │  │  ├─ exception
   │  │           │  │  │  ├─ DietAIException.java
   │  │           │  │  │  ├─ DietNotFoundException.java
   │  │           │  │  │  ├─ DietServerException.java
   │  │           │  │  │  └─ InvalidDietRequestException.java
   │  │           │  │  ├─ request
   │  │           │  │  │  ├─ DietDetailRequest.java
   │  │           │  │  │  ├─ DietListRequest.java
   │  │           │  │  │  └─ DietRecommendationRequest.java
   │  │           │  │  └─ response
   │  │           │  │     ├─ DietDetailResponse.java
   │  │           │  │     ├─ DietListItem.java
   │  │           │  │     ├─ DietListResponse.java
   │  │           │  │     └─ DietRecommendationResponse.java
   │  │           │  ├─ repository
   │  │           │  │  └─ DietJpaRepo.java
   │  │           │  └─ service
   │  │           │     ├─ DietService.java
   │  │           │     ├─ DietServiceImpl.java
   │  │           │     └─ GeminiService.java
   │  │           └─ state
   │  │              ├─ controller
   │  │              │  ├─ StateController.java
   │  │              │  └─ StateExceptionHandler.java
   │  │              ├─ domain
   │  │              │  └─ State.java
   │  │              ├─ dto
   │  │              │  ├─ request
   │  │              │  │  ├─ StateCreateReq.java
   │  │              │  │  └─ StateUpdateReq.java
   │  │              │  └─ response
   │  │              │     └─ StateRes.java
   │  │              ├─ repository
   │  │              │  └─ StateJpaRepo.java
   │  │              └─ service
   │  │                 └─ StateService.java
   │  └─ resources
   │     ├─ application.yml
   │     ├─ insert-diet.sql
   │     └─ insert-state.sql
   └─ test
      └─ java
         └─ com
            └─ example
               └─ demo
                  ├─ DemoApplicationTests.java
                  └─ state
                     └─ controller
                        └─ StateControllerTest.java

```