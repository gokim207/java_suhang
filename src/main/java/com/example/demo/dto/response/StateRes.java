package com.example.demo.dto.response;

import lombok.Getter;

@Getter
public class StateRes {
    private Long stateId;
    private String stateName;
    private String stateDescription;
    private String stateStandard;
    private boolean isMain;
}
