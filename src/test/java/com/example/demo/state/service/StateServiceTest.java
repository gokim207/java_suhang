package com.example.demo.state.service;

import com.example.demo.domain.state.domain.State;
import com.example.demo.domain.state.dto.request.StateCreateReq;
import com.example.demo.domain.state.dto.request.StateUpdateReq;
import com.example.demo.domain.state.repository.StateJpaRepo;
import com.example.demo.domain.state.service.StateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Sql(scripts = "/insert-state.sql")
@DisplayName("StateService 통합 테스트")
class StateServiceTest {

    @Autowired
    private StateService stateService;

    @Autowired
    private StateJpaRepo stateJpaRepo;

    @Test
    @DisplayName("모든 상태 조회 성공 - 7개의 상태가 존재")
    void getAllStates_Success() {
        // when
        List<State> result = stateService.getAllStates();

        // then
        assertThat(result).hasSize(7);
        assertThat(result)
                .extracting(State::getName)
                .containsExactlyInAnyOrder(
                        "거지 다이어트",
                        "3주 다이어트",
                        "고구마 식단",
                        "저탄수화물 다이어트",
                        "간헐적 단식",
                        "채식 다이어트",
                        "체중 감량 집중"
                );
    }

    @Test
    @DisplayName("초기 메인 상태 확인 - '체중 감량 집중'이 메인")
    void checkInitialMainState() {
        // when
        List<State> states = stateService.getAllStates();
        State mainState = states.stream()
                .filter(State::isMain)
                .findFirst()
                .orElse(null);

        // then
        assertThat(mainState).isNotNull();
        assertThat(mainState.getName()).isEqualTo("체중 감량 집중");
        assertThat(mainState.getDescription()).isEqualTo("체중 감소를 목표로 한 다이어트");
        assertThat(mainState.getStandard()).isEqualTo("체중 1kg 감량 목표");
    }

    @Test
    @DisplayName("메인 상태 업데이트 성공 - 거지 다이어트를 메인으로 변경")
    void updateMainState_Success() {
        // given
        State targetState = stateJpaRepo.findAll().stream()
                .filter(s -> s.getName().equals("거지 다이어트"))
                .findFirst()
                .orElseThrow();
        Long targetId = targetState.getId();

        // when
        stateService.updateMainState(targetId);

        // then - ID로 직접 조회하여 최신 상태 확인
        State updatedTargetState = stateJpaRepo.findById(targetId).orElseThrow();
        assertThat(updatedTargetState.isMain()).isTrue();
        assertThat(updatedTargetState.getName()).isEqualTo("거지 다이어트");

        // 다른 모든 상태는 false여야 함
        List<State> allStates = stateJpaRepo.findAll();
        long mainCount = allStates.stream()
                .filter(State::isMain)
                .count();
        assertThat(mainCount).isEqualTo(1);

        // 체중 감량 집중은 더 이상 메인이 아니어야 함
        State previousMain = allStates.stream()
                .filter(s -> s.getName().equals("체중 감량 집중"))
                .findFirst()
                .orElseThrow();
        assertThat(previousMain.isMain()).isFalse();
    }

    @Test
    @DisplayName("메인 상태 업데이트 실패 - 존재하지 않는 ID")
    void updateMainState_NotFound() {
        // given
        Long invalidId = 999L;

        // when & then
        assertThatThrownBy(() -> stateService.updateMainState(invalidId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("id가 정상적으로 입력되지 않았습니다.");

        // 예외 발생으로 트랜잭션이 롤백되므로 기존 메인 상태는 유지되어야 함
        List<State> states = stateJpaRepo.findAll();
        long mainCount = states.stream()
                .filter(State::isMain)
                .count();
        assertThat(mainCount).isEqualTo(1); // 기존 메인 상태("체중 감량 집중") 유지

        // "체중 감량 집중"이 여전히 메인이어야 함
        State mainState = states.stream()
                .filter(State::isMain)
                .findFirst()
                .orElseThrow();
        assertThat(mainState.getName()).isEqualTo("체중 감량 집중");
    }

    @Test
    @DisplayName("상태 삭제 성공 - 3주 다이어트 삭제")
    void deleteState_Success() {
        // given
        State targetState = stateJpaRepo.findAll().stream()
                .filter(s -> s.getName().equals("3주 다이어트"))
                .findFirst()
                .orElseThrow();
        Long targetId = targetState.getId();

        // when
        stateService.deleteState(targetId);

        // then
        List<State> states = stateJpaRepo.findAll();
        assertThat(states).hasSize(6);
        assertThat(states)
                .extracting(State::getName)
                .doesNotContain("3주 다이어트");
    }

    @Test
    @DisplayName("상태 삭제 실패 - 존재하지 않는 ID")
    void deleteState_NotFound() {
        // given
        Long invalidId = 999L;

        // when & then
        assertThatThrownBy(() -> stateService.deleteState(invalidId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("id가 정상적으로 입력되지 않았습니다.");

        // 상태 개수는 변하지 않아야 함
        List<State> states = stateJpaRepo.findAll();
        assertThat(states).hasSize(7);
    }

    @Test
    @DisplayName("상태 생성 성공 - 새로운 다이어트 추가")
    void createState_Success() {
        // given
        StateCreateReq createReq = StateCreateReq.builder()
                .stateName("키토제닉 다이어트")
                .stateDescription("초저탄고지 식단")
                .stateStandard("탄수화물 20g 이하, 지방 75% 이상")
                .build();

        // when
        stateService.createState(createReq);

        // then
        List<State> states = stateJpaRepo.findAll();
        assertThat(states).hasSize(8);

        State newState = states.stream()
                .filter(s -> s.getName().equals("키토제닉 다이어트"))
                .findFirst()
                .orElseThrow();

        assertThat(newState.getDescription()).isEqualTo("초저탄고지 식단");
        assertThat(newState.getStandard()).isEqualTo("탄수화물 20g 이하, 지방 75% 이상");
        assertThat(newState.isMain()).isFalse();
    }

    @Test
    @DisplayName("상태 업데이트 성공 - 고구마 식단 정보 수정")
    void updateState_Success() {
        // given
        State targetState = stateJpaRepo.findAll().stream()
                .filter(s -> s.getName().equals("고구마 식단"))
                .findFirst()
                .orElseThrow();

        StateUpdateReq updateReq = StateUpdateReq.builder()
                .stateId(targetState.getId())
                .stateName("고구마 중심 식단")
                .stateDescription("고구마와 닭가슴살 위주 식단")
                .stateStandard("하루 3끼 고구마 + 단백질 섭취")
                .build();

        // when
        stateService.updateState(updateReq);

        // then
        State updatedState = stateJpaRepo.findById(targetState.getId()).orElseThrow();
        assertThat(updatedState.getName()).isEqualTo("고구마 중심 식단");
        assertThat(updatedState.getDescription()).isEqualTo("고구마와 닭가슴살 위주 식단");
        assertThat(updatedState.getStandard()).isEqualTo("하루 3끼 고구마 + 단백질 섭취");
    }

    @Test
    @DisplayName("상태 업데이트 실패 - 존재하지 않는 ID")
    void updateState_NotFound() {
        // given
        StateUpdateReq updateReq = StateUpdateReq.builder()
                .stateId(999L)
                .stateName("수정된 상태")
                .stateDescription("수정된 설명")
                .stateStandard("수정된 기준")
                .build();

        // when & then
        assertThatThrownBy(() -> stateService.updateState(updateReq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 ID를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("특정 상태 상세 조회 - 간헐적 단식")
    void getSpecificState_Success() {
        // when
        List<State> states = stateService.getAllStates();
        State intermittentFasting = states.stream()
                .filter(s -> s.getName().equals("간헐적 단식"))
                .findFirst()
                .orElseThrow();

        // then
        assertThat(intermittentFasting.getDescription()).isEqualTo("하루 일정 시간만 식사하는 다이어트");
        assertThat(intermittentFasting.getStandard()).isEqualTo("16:8 방식 적용");
        assertThat(intermittentFasting.isMain()).isFalse();
    }

    @Test
    @DisplayName("여러 상태 삭제 후 개수 확인")
    void deleteMultipleStates_Success() {
        // given
        List<State> statesToDelete = stateJpaRepo.findAll().stream()
                .filter(s -> s.getName().equals("거지 다이어트") ||
                        s.getName().equals("채식 다이어트"))
                .toList();

        // when
        statesToDelete.forEach(state -> stateService.deleteState(state.getId()));

        // then
        List<State> remainingStates = stateJpaRepo.findAll();
        assertThat(remainingStates).hasSize(5);
        assertThat(remainingStates)
                .extracting(State::getName)
                .doesNotContain("거지 다이어트", "채식 다이어트");
    }
}