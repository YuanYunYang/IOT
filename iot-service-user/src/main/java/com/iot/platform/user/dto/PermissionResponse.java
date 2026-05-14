package com.iot.platform.user.dto;

import com.iot.platform.user.domain.PermType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@Schema(description = "权限节点（响应）；树形结构通过 children 嵌套。")
public class PermissionResponse {
    @Schema(description = "权限主键 ID")
    private Long id;
    @Schema(description = "父权限 ID")
    private Long parentId;
    @Schema(description = "权限类型")
    private PermType permType;
    @Schema(description = "权限编码")
    private String permCode;
    @Schema(description = "权限名称")
    private String name;
    @Schema(description = "路径")
    private String path;
    @Schema(description = "HTTP 方法")
    private String httpMethod;
    @Schema(description = "排序")
    private Integer sortOrder;
    @Schema(description = "创建时间（UTC）")
    private Instant createdAt;
    @Schema(description = "子节点列表（树接口返回）")
    private List<PermissionResponse> children;
}
