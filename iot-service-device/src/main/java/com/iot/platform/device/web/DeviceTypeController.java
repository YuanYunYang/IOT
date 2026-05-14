package com.iot.platform.device.web;

import com.iot.platform.common.api.ApiResult;
import com.iot.platform.device.domain.IotDeviceType;
import com.iot.platform.device.dto.DeviceTypeRequest;
import com.iot.platform.device.service.DeviceTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/device-types")
@RequiredArgsConstructor
public class DeviceTypeController {

    private final DeviceTypeService deviceTypeService;

    @GetMapping
    public ApiResult<List<IotDeviceType>> list() {
        return ApiResult.ok(deviceTypeService.listAll());
    }

    @GetMapping("/{id}")
    public ApiResult<IotDeviceType> get(@PathVariable Long id) {
        return ApiResult.ok(deviceTypeService.get(id));
    }

    @PostMapping
    public ApiResult<IotDeviceType> create(@Valid @RequestBody DeviceTypeRequest req) {
        return ApiResult.ok(deviceTypeService.create(req));
    }

    @PutMapping("/{id}")
    public ApiResult<IotDeviceType> update(@PathVariable Long id, @Valid @RequestBody DeviceTypeRequest req) {
        return ApiResult.ok(deviceTypeService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        deviceTypeService.delete(id);
        return ApiResult.ok(null);
    }
}

