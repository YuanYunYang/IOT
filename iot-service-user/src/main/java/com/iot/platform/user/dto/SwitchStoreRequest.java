package com.iot.platform.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(description = "切换当前会话门店")
public class SwitchStoreRequest {

    @NotNull
    @Schema(description = "目标门店 ID，须在 accessibleStores 内", required = true)
    private Long storeId;
}
