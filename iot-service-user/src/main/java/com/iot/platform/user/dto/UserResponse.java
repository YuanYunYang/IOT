package com.iot.platform.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@Schema(description = "用户信息（响应）")
public class UserResponse {
    @Schema(description = "用户主键 ID")
    private Long id;
    @Schema(description = "登录用户名")
    private String username;
    @Schema(description = "真实姓名或显示名")
    private String realName;
    @Schema(description = "是否启用")
    private Boolean enabled;
    @Schema(description = "创建时间（UTC）")
    private Instant createdAt;
    @Schema(description = "最近更新时间（UTC）")
    private Instant updatedAt;
    @Schema(description = "已绑定角色编码列表")
    private List<String> roleCodes;

    @Schema(description = "租户 ID")
    private Long tenantId;
    @Schema(description = "租户编码")
    private String tenantCode;
    @Schema(description = "租户类型")
    private String tenantType;
    @Schema(description = "默认登录门店 ID")
    private Long defaultStoreId;
    @Schema(description = "可访问门店 ID 列表")
    private List<Long> accessibleStoreIds;
}
