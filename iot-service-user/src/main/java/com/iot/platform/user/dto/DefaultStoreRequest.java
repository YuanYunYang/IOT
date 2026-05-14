package com.iot.platform.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(description = "设置登录默认门店（下次登录时的初始门店）")
public class DefaultStoreRequest {

    @NotNull
    @Schema(description = "默认门店 ID，须在可访问门店内", required = true)
    private Long storeId;
}
