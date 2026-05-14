package com.iot.platform.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Schema(description = "创建/更新角色请求体")
public class RoleRequest {

    @NotBlank
    @Size(max = 64)
    @Schema(description = "角色编码（唯一标识）", example = "ADMIN", required = true)
    private String roleCode;

    @NotBlank
    @Size(max = 128)
    @Schema(description = "角色显示名称", required = true)
    private String roleName;

    @Size(max = 256)
    @Schema(description = "备注说明")
    private String remark;

    @Schema(description = "绑定的权限 ID 列表")
    private List<Long> permissionIds;
}
