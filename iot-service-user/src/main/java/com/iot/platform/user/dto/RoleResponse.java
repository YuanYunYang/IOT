package com.iot.platform.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@Schema(description = "角色信息（响应）")
public class RoleResponse {
    @Schema(description = "角色主键 ID")
    private Long id;
    @Schema(description = "角色编码")
    private String roleCode;
    @Schema(description = "角色名称")
    private String roleName;
    @Schema(description = "备注说明")
    private String remark;
    @Schema(description = "创建时间（UTC）")
    private Instant createdAt;
    @Schema(description = "绑定的权限 ID 列表")
    private List<Long> permissionIds;
}
