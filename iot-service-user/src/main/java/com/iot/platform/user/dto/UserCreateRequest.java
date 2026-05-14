package com.iot.platform.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Schema(description = "创建用户请求体")
public class UserCreateRequest {

    @NotNull
    @Schema(description = "租户 ID", required = true)
    private Long tenantId;

    @NotBlank
    @Size(max = 64)
    @Schema(description = "登录用户名（租户内唯一）", example = "zhangsan", required = true)
    private String username;

    @NotBlank
    @Size(min = 6, max = 64)
    @Schema(description = "登录密码（明文，服务端会加密存储）", required = true)
    private String password;

    @Size(max = 128)
    @Schema(description = "真实姓名或显示名")
    private String realName;

    @Schema(description = "是否启用，默认 true")
    private Boolean enabled = true;

    @Schema(description = "绑定角色 ID 列表，可为空表示暂不分配角色")
    private List<Long> roleIds;

    @NotEmpty
    @Schema(description = "可访问门店 ID 列表（须属于该租户）", required = true)
    private List<Long> accessibleStoreIds;

    @Schema(description = "默认登录门店 ID，须在可访问列表中；不传则取列表第一个")
    private Long defaultStoreId;
}
