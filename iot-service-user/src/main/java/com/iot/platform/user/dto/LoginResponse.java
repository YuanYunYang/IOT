package com.iot.platform.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "登录成功：JWT + 租户/门店上下文")
public class LoginResponse {

    @Schema(description = "JWT 访问令牌，请求头：Authorization: Bearer {accessToken}")
    private String accessToken;

    @Schema(description = "固定为 Bearer")
    private String tokenType;

    @Schema(description = "过期时间（秒）")
    private long expiresIn;

    @Schema(description = "租户与门店上下文（Claims 中同步 tid/ttp/sid）")
    private AuthProfile profile;
}
