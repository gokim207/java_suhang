package com.example.demo.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

//BaseTimeEntity의 createdAt, updatedAt 자동 설정을 위해 필요함 건들면 깨짐, 주의주의
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}