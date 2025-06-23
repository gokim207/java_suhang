package com.example.row.service;

import com.example.row.dto.RowRequest;
import com.example.row.entity.Row;
import com.example.row.repository.RowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Collections;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RowService {
    private final RowRepository rowRepository;

    public String create(RowRequest dto) {
        try {
            Row row = new Row(dto);
            rowRepository.save(row);
            return "생성에 성공하였습니다";
        } catch (Exception e) {
             log.error("Row 생성 중 오류 발생", e);
            return "생성에 실패하였습니다";
            
        }
    }

    public String update(Long id, RowRequest dto) {
    try {
        Row row = rowRepository.findById(id).orElse(null);
        if (row == null)  return "저장에 실패하였습니다 : 해당하는 id를 가지고 있는 이벤트가 없습니다.";
        row.updateFromDto(dto);
        rowRepository.save(row);
        return "수정에 성공하였습니다";
    } catch (Exception e) {
         log.error("Row 생성 중 오류 발생", e);
        return "수정에 실패하였습니다";
    }
}

    public List<Row> findAll() {
        try {
            return rowRepository.findAll();
        } catch (Exception e) {
            log.error("Row 전체 조회 중 오류 발생", e);
            return Collections.emptyList();
        }
    }
}
