package com.iot.platform.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@Schema(description = "更新用户请求体；未传的字段表示不修改。")
public class UserUpdateRequest {

    /** 不传则不修改密码 */
    @Size(min = 6, max = 64)
    @Schema(description = "新密码；不传则不修改原密码")
    private String password;

    @Size(max = 128)
    @Schema(description = "真实姓名或显示名")
    private String realName;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "绑定角色 ID 列表；传则覆盖原有关联")
    private List<Long> roleIds;

    @Schema(description = "可访问门店 ID 列表；传则覆盖原有关联")
    private List<Long> accessibleStoreIds;

    @Schema(description = "默认登录门店 ID；传 null 可清空默认门店（视业务是否允许）")
    private Long defaultStoreId;
}
