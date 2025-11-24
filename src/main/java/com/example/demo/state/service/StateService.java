package com.example.demo.state.service;

import com.example.demo.state.domain.State;
import com.example.demo.state.dto.request.StateCreateReq;
import com.example.demo.state.dto.request.StateUpdateReq;
import com.example.demo.state.repository.StateJpaRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StateService {

    private final StateJpaRepo stateJpaRepo;

    public List<State> getAllStates() {
        return stateJpaRepo.findAll();
    }

    public void updateMainState(Long stateId) {
        // 1. 모든 isMain을 false로 일괄 업데이트
        stateJpaRepo.updateAllIsMainToFalse();

        // 2. 특정 ID만 true로 설정
       stateJpaRepo.findById(stateId)
                .orElseThrow(() -> new IllegalArgumentException("id가 정상적으로 입력되지 않았습니다."));

        stateJpaRepo.updateIsMainById(stateId, true);
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
