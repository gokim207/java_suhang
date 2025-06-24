package com.example.row.repository;

import com.example.row.entity.Row;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RowRepository extends JpaRepository<Row, Long> {
    List<Row> findByIsCompleted(boolean isCompleted);
}