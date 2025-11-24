package com.example.demo.state.controller;

import com.example.demo.state.domain.State;
import com.example.demo.state.dto.request.StateCreateReq;
import com.example.demo.state.dto.request.StateUpdateReq;
import com.example.demo.state.service.StateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/state")
public class StateController {

    private final StateService stateService;

    @GetMapping
    public List<State> getAllStates() {
        return stateService.getAllStates();
    }

    @PutMapping("/update/main")
    public ResponseEntity<String> updateMainState(@RequestParam Long stateId) {
        stateService.updateMainState(stateId);

        return ResponseEntity.ok("성공적으로 상태가 변경되었습니다.");
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateState(@RequestBody StateUpdateReq stateUpdateReq) {
        stateService.updateState(stateUpdateReq);

        return ResponseEntity.ok("성공적으로 저장되었습니다.");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteState(@RequestParam Long stateId) {
        stateService.deleteState(stateId);

        return ResponseEntity.ok("성공적으로 삭제했습니다.");
    }

    @PostMapping("/create")
    public ResponseEntity<String> createState(@RequestBody StateCreateReq state) {
        stateService.createState(state);

        return ResponseEntity.ok("성공적으로 생성되었습니다.");
    }
}
