package com.iot.platform.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "门店简要信息")
public class StoreBrief {

    @Schema(description = "门店 ID")
    private Long id;
    @Schema(description = "租户内门店编码")
    private String storeCode;
    @Schema(description = "门店名称")
    private String storeName;
    @Schema(description = "是否启用")
    private Boolean enabled;
}
