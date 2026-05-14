package com.iot.platform.device.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class DevicePointRequest {

    @NotNull
    private Long deviceTypeId;

    @NotBlank
    @Size(max = 128)
    private String pointCode;

    @NotBlank
    @Size(max = 128)
    private String pointName;

    @Size(max = 32)
    private String unit;

    @Size(max = 32)
    private String valueType = "number";

    @Size(max = 128)
    private String externalPointId;

    @NotNull
    private Boolean enabled = true;

    @NotNull
    private Integer sortOrder = 0;

    @Size(max = 256)
    private String remark;
}

