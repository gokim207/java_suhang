package com.example.demo.diet.repository;

import com.example.demo.diet.domain.Dist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DietJpaRepo extends JpaRepository<Dist, Long> {

    /**
     * dietId로 식단 조회
     */
    Optional<Dist> findByDietId(Long dietId);

    /**
     * 모든 식단을 생성일 기준 오름차순으로 조회
     */
    List<Dist> findAllByOrderByCreatedAtAsc();

    /**
     * 모든 식단을 생성일 기준 내림차순으로 조회
     */
    List<Dist> findAllByOrderByCreatedAtDesc();

}