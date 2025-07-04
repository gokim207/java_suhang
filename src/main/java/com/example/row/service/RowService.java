package com.example.row.service;

import com.example.row.dto.RowRequest;
import com.example.row.dto.RowResponse;
import com.example.row.entity.Row;
import com.example.row.repository.RowRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
            if (row == null) return "저장에 실패하였습니다 : 해당하는 id를 가지고 있는 이벤트가 없습니다.";
            row.updateFromDto(dto);
            rowRepository.save(row);
            return "수정에 성공하였습니다";
        } catch (Exception e) {
            log.error("Row 수정 중 오류 발생", e);
            return "수정에 실패하였습니다";
        }
    }

   public List<RowResponse> findAllOrderByDDayAsc() {
        List<Row> events = rowRepository.findAll();
        return events.stream()
                .sorted(Comparator.comparing(event -> ChronoUnit.DAYS.between(LocalDate.now(), event.getTargetDate())))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<RowResponse> findAllOrderByDDayDesc() {
            List<Row> events = rowRepository.findAll();
            return events.stream()
                    .sorted(Comparator.comparing((Row event) -> ChronoUnit.DAYS.between(LocalDate.now(), event.getTargetDate())).reversed())
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }

    private RowResponse toResponse(Row row) {
        int dDay = (int) ChronoUnit.DAYS.between(LocalDate.now(), row.getTargetDate());

        RowResponse response = new RowResponse(row);
        response.setId(row.getId());
        response.setName(row.getName());
        response.setContent(row.getContent());
        response.setTargetDate(LocalDate.from(LocalDate.from(row.getTargetDate()).atStartOfDay()));
        response.setCreatedAt(row.getCreatedAt());
        response.setDDay(dDay);

        return response;
    }
    public List<RowResponse> findAll() {
        List<Row> entities = rowRepository.findAll();
        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public RowResponse findById(Long id) {
        Row row = rowRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Row를 찾을 수 없습니다. ID: " + id));
        return toResponse(row);
    }


    public void delete(Long id) {
        if (!rowRepository.existsById(id)) {
            throw new IllegalArgumentException("Row를 찾을 수 없습니다. ID: " + id);
        }
        rowRepository.deleteById(id);
    }

    public void markEventAsCompleted(Long id) {
        Row row = rowRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 이벤트를 찾을 수 없습니다."));

        if (row.isCompleted()) {
            throw new IllegalStateException("이미 완료된 이벤트입니다.");
        }

        row.setCompleted(true);
        rowRepository.save(row);
    }

    public List<RowResponse> findByCompletionStatus(boolean isCompleted) {
        List<Row> events = rowRepository.findByIsCompleted(isCompleted);

        return events.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}