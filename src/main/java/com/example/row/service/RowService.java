package com.example.row.service;

import com.example.row.dto.RowRequest;
import com.example.row.entity.Row;
import com.example.row.repository.RowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RowService {
    private final RowRepository rowRepository;

    public String create(RowRequest dto) {
        try {
            Row row = new Row(dto); 
            rowRepository.save(row);
            return "저장에 성공하였습니다";
        } catch (Exception e) {
            return "저장에 성공하였습니다";
        }
    }

    public String update(RowRequest dto) {
        try {
            Row row = rowRepository.findById(dto.getId()).orElse(null);
            if (row == null) return false;
            row.updateFromDto(dto);
            rowRepository.save(row);
            return "저장에 성공하였습니다";
        } catch (Exception e) {
            return "저장에 실패하였습니다";
        }
    }
}