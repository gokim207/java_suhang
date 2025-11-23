package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StateUpdateReq {
    private Long stateId;
    private String stateName;
    private String stateDescription;
    private String stateStandard;
}
