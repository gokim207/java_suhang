package com.example.demo.domain.state.dto.request;

import com.example.demo.domain.state.domain.State;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StateCreateReq {
    private String stateName;
    private String stateDescription;
    private String stateStandard;

    @JsonProperty("isMain")
    private boolean isMain;

    public State from() {
        return State.builder()
                .name(stateName)
                .description(stateDescription)
                .standard(stateStandard)
                .isMain(isMain)
                .build();
    }
}
