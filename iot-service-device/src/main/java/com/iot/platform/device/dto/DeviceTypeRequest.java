package com.iot.platform.device.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class DeviceTypeRequest {

    @NotBlank
    @Size(max = 64)
    private String typeCode;

    @NotBlank
    @Size(max = 128)
    private String typeName;

    @Size(max = 128)
    private String subsystem;

    @Size(max = 256)
    private String remark;
}

