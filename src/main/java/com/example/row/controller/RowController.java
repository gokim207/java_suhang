package com.example.row.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import com.example.row.service.RowService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/row")
public class RowController {
    private final RowService rowService;

    @PostMapping("/create")
    public String create(@RequestBody RowRequest dto) {
        return rowService.create(dto) ? "created" : "fail";
    }

    @PostMapping("/update")
    public String update(@RequestBody RowRequest dto) {
        return rowService.update(dto) ? "updated" : "fail";
    }
}