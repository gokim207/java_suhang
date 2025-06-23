package com.example.row.controller;
import com.example.row.dto.RowRequest;
import com.example.row.entity.Row;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import com.example.row.service.RowService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
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
@RequiredArgsConstructor
@RequestMapping("/")
@Tag(name = "Row API", description = "Row 관련 API")
public class RowController {
    private final RowService rowService;

    @PostMapping("create")
    @Operation(summary = "Row 생성", description = "Row 데이터를 생성합니다.")
    public String create(@RequestBody RowRequest dto) {
        return rowService.create(dto);
    }

    @PostMapping("update")
    @Operation(summary = "Row 수정", description = "Row 데이터를 수정합니다.")
    public String update(@RequestParam Long id, @RequestBody RowRequest dto) {
        return rowService.update(id, dto);
    }

    //테스트 용이였어서 주석 처리함
    //  @GetMapping("getAll")
    //  @Operation(summary = "Row 전체 조회", description = "모든 Row 데이터를 조회합니다.")
    //  public List<Row> list() {
    //      return rowService.findAll();
    //  }

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
