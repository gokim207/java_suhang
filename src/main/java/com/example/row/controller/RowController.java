package com.example.row.controller;
import com.example.row.dto.RowRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import com.example.row.service.RowService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.example.row.dto.RowResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/event")
@Tag(name = "Row API", description = "Row 관련 API")
public class RowController {
    private final RowService rowService;

    @PostMapping("create")
    @Operation(summary = "Row 생성", description = "Row 데이터를 생성합니다.")
    public String create(@RequestBody RowRequest dto) {
        return rowService.create(dto);
    }

    @PatchMapping("update")
    @Operation(summary = "Row 수정", description = "Row 데이터를 수정합니다.")
    public String update(@RequestParam Long id, @RequestBody RowRequest dto) {
        return rowService.update(id, dto);
    }

    @GetMapping("/sortedBy")
    public ResponseEntity<List<RowResponse>> getRow(@RequestParam String sort) {
        List<RowResponse> events;

        if ("asc".equalsIgnoreCase(sort)) { // dDay 기준 내림차순 또는 오름차순
            events = rowService.findAllOrderByDDayAsc();
        } else {
            events = rowService.findAllOrderByDDayDesc();
        }

        return ResponseEntity.ok(events);
    }
}
