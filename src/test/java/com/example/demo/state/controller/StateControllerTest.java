package com.example.demo.state.controller;

import com.example.demo.domain.state.domain.State;
import com.example.demo.domain.state.dto.request.StateCreateReq;
import com.example.demo.domain.state.dto.request.StateUpdateReq;
import com.example.demo.domain.state.repository.StateJpaRepo;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "/insert-state.sql")
class StateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StateJpaRepo stateJpaRepo;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("전체 상태 조회 - 성공")
    void getAllStates_Success() throws Exception {
        mockMvc.perform(get("/state"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(7))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].description").exists())
                .andExpect(jsonPath("$[0].standard").exists())
                .andExpect(jsonPath("$[0].main").exists());
    }

    @Test
    @DisplayName("메인 상태 변경 - 성공")
    void updateMainState_Success() throws Exception {
        // '체중 감량 집중'이 현재 메인 상태 (isMain=true)
        State mainState = stateJpaRepo.findAll().stream()
                .filter(State::isMain)
                .findFirst()
                .orElseThrow();

        State targetState = stateJpaRepo.findAll().stream()
                .filter(s -> !s.isMain())
                .findFirst()
                .orElseThrow();

        mockMvc.perform(put("/state/update/main")
                        .param("stateId", targetState.getId().toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("성공적으로 상태가 변경되었습니다."));

        entityManager.flush();
        entityManager.clear();

        State updatedMainState = stateJpaRepo.findById(mainState.getId()).orElseThrow();
        State updatedTargetState = stateJpaRepo.findById(targetState.getId()).orElseThrow();

        assertThat(updatedMainState.isMain()).isFalse();
        assertThat(updatedTargetState.isMain()).isTrue();
    }

    @Test
    @DisplayName("메인 상태 변경 - 존재하지 않는 ID로 실패")
    void updateMainState_Fail_InvalidId() throws Exception {
        mockMvc.perform(put("/state/update/main")
                        .param("stateId", "99999"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("id가 정상적으로 입력되지 않았습니다."));
    }

    @Test
    @DisplayName("상태 수정 - 성공")
    void updateState_Success() throws Exception {
        State existingState = stateJpaRepo.findAll().get(0);

        StateUpdateReq updateReq = new StateUpdateReq(
                existingState.getId(),
                "수정된 상태",
                "수정된 설명",
                "수정된 기준"
        );

        mockMvc.perform(put("/state/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("성공적으로 저장되었습니다."));

        State updatedState = stateJpaRepo.findById(existingState.getId()).orElseThrow();
        assertThat(updatedState.getName()).isEqualTo("수정된 상태");
        assertThat(updatedState.getDescription()).isEqualTo("수정된 설명");
        assertThat(updatedState.getStandard()).isEqualTo("수정된 기준");
    }

    @Test
    @DisplayName("상태 수정 - 존재하지 않는 ID로 실패")
    void updateState_Fail_InvalidId() throws Exception {
        StateUpdateReq updateReq = new StateUpdateReq(
                99999L,
                "대기중",
                "대기 상태",
                "진행률 0%"
        );

        mockMvc.perform(put("/state/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("해당 ID를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("상태 삭제 - 성공")
    void deleteState_Success() throws Exception {
        State stateToDelete = stateJpaRepo.findAll().stream()
                .filter(s -> !s.isMain())
                .findFirst()
                .orElseThrow();

        mockMvc.perform(delete("/state/delete")
                        .param("stateId", stateToDelete.getId().toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("성공적으로 삭제했습니다."));

        assertThat(stateJpaRepo.findById(stateToDelete.getId())).isEmpty();
    }

    @Test
    @DisplayName("상태 삭제 - 존재하지 않는 ID로 실패")
    void deleteState_Fail_InvalidId() throws Exception {
        mockMvc.perform(delete("/state/delete")
                        .param("stateId", "99999"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("id가 정상적으로 입력되지 않았습니다."));
    }

    @Test
    @DisplayName("상태 생성 - 성공")
    void createState_Success() throws Exception {
        StateCreateReq createReq = new StateCreateReq(
                "보류",
                "작업이 보류된 상태",
                "진행률 30% 미만",
                false
        );

        mockMvc.perform(post("/state/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("성공적으로 생성되었습니다."));

        assertThat(stateJpaRepo.findAll()).hasSize(8);
        assertThat(stateJpaRepo.findAll())
                .extracting("name")
                .contains("보류");
    }

    @Test
    @DisplayName("상태 생성 - isMain=true")
    void createState_WithMainTrue() throws Exception {
        StateCreateReq createReq = new StateCreateReq(
                "긴급",
                "긴급 처리가 필요한 상태",
                "우선순위 최상",
                true
        );

        mockMvc.perform(post("/state/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("성공적으로 생성되었습니다."));

        assertThat(stateJpaRepo.findAll())
                .filteredOn("name", "긴급")
                .extracting("main")
                .containsExactly(true);
    }
}