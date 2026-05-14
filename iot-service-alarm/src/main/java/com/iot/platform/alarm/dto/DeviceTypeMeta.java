package com.iot.platform.alarm.dto;

import lombok.Data;

@Data
public class DeviceTypeMeta {
    private Long id;
    private String typeCode;
    private String typeName;
    private String subsystem;
}

