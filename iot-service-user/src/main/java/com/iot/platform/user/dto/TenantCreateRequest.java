package com.iot.platform.user.dto;

import com.iot.platform.user.domain.TenantType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Schema(description = "创建租户")
public class TenantCreateRequest {

    @NotBlank
    @Size(max = 64)
    @Schema(description = "租户编码，全局唯一", required = true)
    private String tenantCode;

    @NotBlank
    @Size(max = 128)
    @Schema(description = "租户名称", required = true)
    private String tenantName;

    @NotNull
    @Schema(description = "ENTERPRISE_MULTI_STORE 或 SINGLE_STORE", required = true)
    private TenantType tenantType;
}
