package com.iot.platform.core.web;

import com.iot.platform.common.api.ApiResult;
import com.iot.platform.core.client.dto.AlarmRecordViewDto;
import com.iot.platform.core.client.dto.DeviceViewDto;
import com.iot.platform.core.client.dto.RoleViewDto;
import com.iot.platform.core.client.dto.UserViewDto;
import com.iot.platform.core.service.IntegrationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 经网关访问示例：/api/v1/core/integration/users（Rewrite 后为 /api/v1/integration/users）。
 */
@RestController
@RequestMapping("/api/v1/integration")
@RequiredArgsConstructor
public class IntegrationQueryController {

    private final IntegrationQueryService integrationQueryService;

    @GetMapping("/users")
    public ApiResult<List<UserViewDto>> listUsers() {
        return ApiResult.ok(integrationQueryService.listUsers());
    }

    @GetMapping("/users/{id}")
    public ApiResult<UserViewDto> getUser(@PathVariable Long id) {
        return ApiResult.ok(integrationQueryService.getUser(id));
    }

    @GetMapping("/roles")
    public ApiResult<List<RoleViewDto>> listRoles() {
        return ApiResult.ok(integrationQueryService.listRoles());
    }

    @GetMapping("/roles/{id}")
    public ApiResult<RoleViewDto> getRole(@PathVariable Long id) {
        return ApiResult.ok(integrationQueryService.getRole(id));
    }

    @GetMapping("/devices")
    public ApiResult<List<DeviceViewDto>> listDevices() {
        return ApiResult.ok(integrationQueryService.listDevices());
    }

    @GetMapping("/devices/{id}")
    public ApiResult<DeviceViewDto> getDevice(@PathVariable Long id) {
        return ApiResult.ok(integrationQueryService.getDevice(id));
    }

    @GetMapping("/alarm-records")
    public ApiResult<List<AlarmRecordViewDto>> listAlarmRecords(@RequestParam(required = false) Long ruleId) {
        return ApiResult.ok(integrationQueryService.listAlarmRecords(ruleId));
    }

    @GetMapping("/alarm-records/{id}")
    public ApiResult<AlarmRecordViewDto> getAlarmRecord(@PathVariable Long id) {
        return ApiResult.ok(integrationQueryService.getAlarmRecord(id));
    }
}
