package com.iot.platform.core.client;

import com.iot.platform.common.api.ApiResult;
import com.iot.platform.core.client.dto.DeviceViewDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "iot-service-device", path = "/api/v1/devices")
public interface DeviceFeignClient {

    @GetMapping
    ApiResult<List<DeviceViewDto>> listDevices();

    @GetMapping("/{id}")
    ApiResult<DeviceViewDto> getDevice(@PathVariable("id") Long id);
}
