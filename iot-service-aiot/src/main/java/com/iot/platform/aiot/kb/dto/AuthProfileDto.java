package com.iot.platform.aiot.kb.dto;

import lombok.Data;

import java.util.List;

/**
 * 与 iot-service-user 的 AuthProfile JSON 对齐，供 Feign 反序列化。
 */
@Data
public class AuthProfileDto {

    private Long tenantId;
    private String tenantCode;
    private String tenantType;
    private Long currentStoreId;
    private Long defaultStoreId;
    private boolean storeSwitchable;
    private List<StoreBriefDto> accessibleStores;
}
