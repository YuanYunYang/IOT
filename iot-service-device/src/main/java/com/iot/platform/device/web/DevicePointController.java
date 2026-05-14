package com.iot.platform.device.web;

import com.iot.platform.common.api.ApiResult;
import com.iot.platform.device.domain.IotDevicePoint;
import com.iot.platform.device.dto.DevicePointRequest;
import com.iot.platform.device.service.DevicePointService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/device-points")
@RequiredArgsConstructor
public class DevicePointController {

    private final DevicePointService devicePointService;

    @GetMapping
    public ApiResult<List<IotDevicePoint>> listByType(@RequestParam("deviceTypeId") Long deviceTypeId) {
        return ApiResult.ok(devicePointService.listByType(deviceTypeId));
    }

    @GetMapping("/{id}")
    public ApiResult<IotDevicePoint> get(@PathVariable Long id) {
        return ApiResult.ok(devicePointService.get(id));
    }

    @PostMapping
    public ApiResult<IotDevicePoint> create(@Valid @RequestBody DevicePointRequest req) {
        return ApiResult.ok(devicePointService.create(req));
    }

    @PutMapping("/{id}")
    public ApiResult<IotDevicePoint> update(@PathVariable Long id, @Valid @RequestBody DevicePointRequest req) {
        return ApiResult.ok(devicePointService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        devicePointService.delete(id);
        return ApiResult.ok(null);
    }
}

