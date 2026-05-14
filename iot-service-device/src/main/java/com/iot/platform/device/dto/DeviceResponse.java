package com.iot.platform.device.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class DeviceResponse {

    private Long id;
    private String subsystem;

    private Long deviceTypeId;
    private String deviceTypeName;

    private String deviceName;
    private String location;
    private String externalDeviceId;
    private String remark;

    private Instant createdAt;
    private Instant updatedAt;
}

