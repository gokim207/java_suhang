package com.example.demo.dto.request;

import com.example.demo.domain.State;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StateCreateReq {
    private String stateName;
    private String stateDescription;
    private String stateStandard;
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
