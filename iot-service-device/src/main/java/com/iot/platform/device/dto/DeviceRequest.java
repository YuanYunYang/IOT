package com.iot.platform.device.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class DeviceRequest {

    @Size(max = 128)
    private String subsystem;

    private Long deviceTypeId;

    @NotBlank
    @Size(max = 256)
    private String deviceName;

    @Size(max = 256)
    private String location;

    @Size(max = 128)
    private String externalDeviceId;

    @Size(max = 512)
    private String remark;
}
