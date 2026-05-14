package com.iot.platform.device.web;

import com.iot.platform.common.api.ApiResult;
import com.iot.platform.device.dto.DeviceRequest;
import com.iot.platform.device.dto.DeviceResponse;
import com.iot.platform.device.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    public ApiResult<List<DeviceResponse>> list() {
        return ApiResult.ok(deviceService.listAll());
    }

    @GetMapping("/{id}")
    public ApiResult<DeviceResponse> get(@PathVariable Long id) {
        return ApiResult.ok(deviceService.get(id));
    }

    @PostMapping
    public ApiResult<DeviceResponse> create(@Valid @RequestBody DeviceRequest req) {
        return ApiResult.ok(deviceService.create(req));
    }

    @PutMapping("/{id}")
    public ApiResult<DeviceResponse> update(@PathVariable Long id, @Valid @RequestBody DeviceRequest req) {
        return ApiResult.ok(deviceService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        deviceService.delete(id);
        return ApiResult.ok(null);
    }
}
