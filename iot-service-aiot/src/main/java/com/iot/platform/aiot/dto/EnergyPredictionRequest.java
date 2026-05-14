package com.iot.platform.aiot.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class EnergyPredictionRequest {

    @NotBlank
    private String deviceId;

    /** 预测时长窗口描述，例如未来 24 小时，默认 24h */
    private String horizon = "24h";
}
