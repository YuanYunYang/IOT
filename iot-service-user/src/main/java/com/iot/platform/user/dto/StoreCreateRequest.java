package com.iot.platform.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Schema(description = "创建门店（隶属于租户）")
public class StoreCreateRequest {

    @NotBlank
    @Size(max = 64)
    @Schema(description = "租户内门店编码", required = true)
    private String storeCode;

    @NotBlank
    @Size(max = 128)
    @Schema(description = "门店名称", required = true)
    private String storeName;

    @Schema(description = "排序，越小越靠前，默认 0")
    private Integer sortOrder;
}
