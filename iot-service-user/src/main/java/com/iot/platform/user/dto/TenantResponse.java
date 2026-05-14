package com.iot.platform.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@Schema(description = "租户信息")
public class TenantResponse {

    private Long id;
    private String tenantCode;
    private String tenantName;
    private String tenantType;
    private Boolean enabled;
    private Instant createdAt;
}
