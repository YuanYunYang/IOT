package com.iot.platform.user.dto;

import com.iot.platform.user.domain.PermType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Schema(description = "创建/更新权限请求体")
public class PermissionRequest {

    @Schema(description = "父权限 ID；顶级节点可为 null 或 0")
    private Long parentId;

    @NotNull
    @Schema(description = "权限类型：菜单/页面/按钮", required = true)
    private PermType permType;

    @NotBlank
    @Size(max = 128)
    @Schema(description = "权限编码（唯一）", required = true)
    private String permCode;

    @NotBlank
    @Size(max = 128)
    @Schema(description = "权限显示名称", required = true)
    private String name;

    @Size(max = 256)
    @Schema(description = "前端路由或后端路径模式，视类型而定")
    private String path;

    @Size(max = 16)
    @Schema(description = "HTTP 方法，如 GET、POST；菜单级可为空")
    private String httpMethod;

    @Schema(description = "同级排序，数值越小越靠前")
    private Integer sortOrder = 0;
}
