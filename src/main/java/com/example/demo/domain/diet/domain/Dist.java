package com.example.demo.domain.diet.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "diet")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dist extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diet_id", updatable = false)
    private Long dietId;

    @Column(name = "diet_content", columnDefinition = "TEXT")
    private String dietContent;

    @Column(name = "diet_name")
    private String dietName;

    @Column(name = "diets_state_name")
    private String dietStateName;

    @Column(name = "request", columnDefinition = "TEXT")
    private String request;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Builder
    public Dist(String dietContent, String dietName, String dietStateName,
                String request, String content) {
        this.dietContent = dietContent;
        this.dietName = dietName;
        this.dietStateName = dietStateName;
        this.request = request;
        this.content = content;
    }

    // 비즈니스 로직: content 업데이트
    public void updateContent(String content) {
        this.content = content;
    }

    // 비즈니스 로직: 전체 정보 업데이트
    public void updateDiet(String dietContent, String dietName, String request) {
        this.dietContent = dietContent;
        this.dietName = dietName;
        this.request = request;
    }
}