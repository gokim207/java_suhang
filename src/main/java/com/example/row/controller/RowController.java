package com.example.row.controller;

import com.example.row.dto.RowResponse;
import com.example.row.service.RowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class RowController {
    @Autowired
    private RowService rowService;

    @GetMapping("/event")
    public ResponseEntity<List<RowResponse>> getRow(@RequestParam String sort) {
        List<RowResponse> events;

        if ("dday".equalsIgnoreCase(sort)) {
            events = rowService.findAllOrderByDDayAsc();
        } else {
            events = rowService.findAll(); // 기본 정렬 또는 전체 조회
        }

        return ResponseEntity.ok(events);
    }
}
