package com.example.row.service;

import com.example.row.dto.RowResponse;
import com.example.row.entity.Row;
import com.example.row.repository.RowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RowService {
    @Autowired
    private RowRepository rowRepository;

    public List<RowResponse> findAllOrderByDDayAsc() {
        List<Row> events = rowRepository.findAll();
        return events.stream()
                .sorted(Comparator.comparing(event -> ChronoUnit.DAYS.between(LocalDate.now(), event.getTargetDate())))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<RowResponse> findAll() {
        List<Row> events = rowRepository.findAll();

        return events.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private RowResponse toResponse(Row row) {
        int dDay = (int) ChronoUnit.DAYS.between(LocalDate.now(), row.getTargetDate());

        RowResponse response = new RowResponse();
        response.setId(row.getId());
        response.setName(row.getName());
        response.setContent(row.getContent());
        response.setTargetDate(row.getTargetDate());
        response.setCreatedAt(row.getCreatedAt());
        response.setDDay(dDay);

        return response;
    }
}
