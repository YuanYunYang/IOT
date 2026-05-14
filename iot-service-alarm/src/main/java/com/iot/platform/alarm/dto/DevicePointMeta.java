package com.iot.platform.alarm.dto;

import lombok.Data;

@Data
public class DevicePointMeta {
    private Long id;
    private Long deviceTypeId;
    private String pointCode;
    private String pointName;
    private String unit;
    private String valueType;
    private String externalPointId;
    private Boolean enabled;
    private Integer sortOrder;
}

