package com.example.demo.domain.state.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StateUpdateReq {
    private Long stateId;
    private String stateName;
    private String stateDescription;
    private String stateStandard;
}
