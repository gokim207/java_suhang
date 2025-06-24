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
    @Operation(summary = "Row 정렬(오름차순 / 내림차순)", description = "asc/desc로 정렬합니다.")
    public ResponseEntity<List<RowResponse>> getRow(@RequestParam String sort) {
        List<RowResponse> events;

        if ("asc".equalsIgnoreCase(sort)) { // dDay 기준 내림차순 또는 오름차순
            events = rowService.findAllOrderByDDayAsc();
        } else {
            events = rowService.findAllOrderByDDayDesc();
        }

        return ResponseEntity.ok(events);
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "Row 완료", description = "Row 데이터를 완료합니다.")
    public ResponseEntity<String> completeEvent(@PathVariable Long id) {
        rowService.markEventAsCompleted(id);
        return ResponseEntity.ok("이벤트가 완료 처리되었습니다.");
    }

    @GetMapping("/completed")
    @Operation(summary = "Row 완료 전체 조회", description = "모든 완료된 Row 데이터를 조회합니다.")
    public ResponseEntity<List<RowResponse>> getCompletedEvents() {
        List<RowResponse> events = rowService.findByCompletionStatus(true);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/ongoing")
    @Operation(summary = "Row 진행중 전체 조회", description = "모든 진행중 Row 데이터를 생성합니다.")
    public ResponseEntity<List<RowResponse>> getOngoingEvents() {
        List<RowResponse> events = rowService.findByCompletionStatus(false);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/all")
    @Operation(summary = "전체 조회", description = "모든 Row 데이터를 조회합니다.")
    public ResponseEntity<List<RowResponse>> getAllRows() {
        List<RowResponse> rows = rowService.findAll();
        return ResponseEntity.ok(rows);
    }

    @GetMapping("/{id}")
    @Operation(summary = "ID로 조회", description = "특정 ID에 해당하는 Row 데이터를 조회합니다.")
    public ResponseEntity<RowResponse> getRowById(@PathVariable Long id) {
        RowResponse row = rowService.findById(id);
        return ResponseEntity.ok(row);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Row 삭제", description = "특정 ID에 해당하는 Row 데이터를 삭제합니다.")
    public ResponseEntity<String> deleteRow(@PathVariable Long id) {
        rowService.delete(id);
        return ResponseEntity.ok("Row 삭제 완료");
    }
}
