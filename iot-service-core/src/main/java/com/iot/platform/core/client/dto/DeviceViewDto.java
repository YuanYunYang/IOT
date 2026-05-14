package com.iot.platform.core.client.dto;

import lombok.Data;

import java.time.Instant;

/**
 * 与 device 服务 DeviceResponse 的 JSON 字段对齐，供 Feign 反序列化。
 */
@Data
public class DeviceViewDto {
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
