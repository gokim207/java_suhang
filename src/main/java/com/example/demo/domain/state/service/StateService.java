package com.example.demo.domain.state.service;

import com.example.demo.domain.state.domain.State;
import com.example.demo.domain.state.dto.request.StateCreateReq;
import com.example.demo.domain.state.dto.request.StateUpdateReq;
import com.example.demo.domain.state.repository.StateJpaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StateService {

    private final StateJpaRepo stateJpaRepo;

    public List<State> getAllStates() {
        return stateJpaRepo.findAll();
    }

    @Transactional
    public void updateMainState(Long stateId) {
        // 1. 대상 State가 존재하는지 먼저 확인
        State targetState = stateJpaRepo.findById(stateId)
                .orElseThrow(() -> new IllegalArgumentException("id가 정상적으로 입력되지 않았습니다."));

        // 2. 모든 State 조회 후 isMain을 false로 설정
        List<State> allStates = stateJpaRepo.findAll();
        allStates.forEach(state -> state.setMain(false));

        // 3. 대상 State만 true로 설정
        targetState.setMain(true);
    }

    public void deleteState(Long stateId) {
        stateJpaRepo.findById(stateId)
                .orElseThrow(() -> new IllegalArgumentException("id가 정상적으로 입력되지 않았습니다."));
        stateJpaRepo.deleteById(stateId);
    }

    public void createState(StateCreateReq state) {
        stateJpaRepo.save(state.from());
    }

    public void updateState(StateUpdateReq stateUpdateReq) {
        State state = stateJpaRepo.findById(stateUpdateReq.getStateId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID를 찾을 수 없습니다."));

        // State 엔티티의 필드들을 업데이트
        state.setName(stateUpdateReq.getStateName());
        state.setDescription(stateUpdateReq.getStateDescription());
        state.setStandard(stateUpdateReq.getStateStandard());

        stateJpaRepo.save(state);
    }
}
