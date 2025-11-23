package com.example.demo.repository;

import com.example.demo.domain.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StateJpaRepo extends JpaRepository<State, Long> {
    @Modifying
    @Query("UPDATE State s" +
            " SET s.isMain = false " +
            "WHERE s.isMain = true")
    void updateAllIsMainToFalse();

    @Modifying
    @Query("UPDATE State s " +
            "SET s.isMain = :isMain " +
            "WHERE s.id = :id")
    void updateIsMainById(@Param("id") Long id, @Param("isMain") boolean isMain);
}
