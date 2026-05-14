package com.iot.platform.alarm.dto;

import com.iot.platform.alarm.domain.CompareOperator;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AlarmRuleRequest {

    @NotBlank
    @Size(max = 256)
    private String name;

    @NotNull
    private Boolean enabled = true;

    @Size(max = 128)
    private String subsystem;

    private Long deviceTypeId;

    @Size(max = 128)
    private String deviceTypeName;

    private Long deviceId;

    @Size(max = 256)
    private String deviceName;

    @Size(max = 128)
    private String pointCode;

    private Long pointId;

    @Size(max = 128)
    private String pointName;

    @NotNull
    private CompareOperator compareOperator;

    @NotBlank
    @Size(max = 64)
    private String thresholdValue;

    @NotNull
    private Integer durationSeconds;

    @NotBlank
    @Size(max = 512)
    private String telemetryRedisKey;

    @Size(max = 256)
    private String alarmTitle;

    @Size(max = 32)
    private String alarmLevel = "WARN";
}
