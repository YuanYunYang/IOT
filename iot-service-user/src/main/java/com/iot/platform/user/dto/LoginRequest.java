package com.iot.platform.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "登录请求（租户内用户名唯一）")
public class LoginRequest {

    @NotBlank
    @Schema(description = "租户编码，对应 sys_tenant.tenant_code", example = "demo-corp", required = true)
    private String tenantCode;

    @NotBlank
    @Schema(description = "用户名（租户内唯一）", example = "admin", required = true)
    private String username;

    @NotBlank
    @Schema(description = "密码", required = true)
    private String password;
}
