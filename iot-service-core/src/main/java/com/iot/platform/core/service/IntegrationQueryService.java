package com.iot.platform.core.service;

import com.iot.platform.common.api.ApiResult;
import com.iot.platform.core.client.AlarmRecordFeignClient;
import com.iot.platform.core.client.DeviceFeignClient;
import com.iot.platform.core.client.RoleAdminFeignClient;
import com.iot.platform.core.client.UserAdminFeignClient;
import com.iot.platform.core.client.dto.AlarmRecordViewDto;
import com.iot.platform.core.client.dto.DeviceViewDto;
import com.iot.platform.core.client.dto.RoleViewDto;
import com.iot.platform.core.client.dto.UserViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IntegrationQueryService {

    private final UserAdminFeignClient userAdminFeignClient;
    private final RoleAdminFeignClient roleAdminFeignClient;
    private final DeviceFeignClient deviceFeignClient;
    private final AlarmRecordFeignClient alarmRecordFeignClient;

    public List<UserViewDto> listUsers() {
        return requireOk(userAdminFeignClient.listUsers());
    }

    public UserViewDto getUser(Long id) {
        return requireOk(userAdminFeignClient.getUser(id));
    }

    public List<RoleViewDto> listRoles() {
        return requireOk(roleAdminFeignClient.listRoles());
    }

    public RoleViewDto getRole(Long id) {
        return requireOk(roleAdminFeignClient.getRole(id));
    }

    public List<DeviceViewDto> listDevices() {
        return requireOk(deviceFeignClient.listDevices());
    }

    public DeviceViewDto getDevice(Long id) {
        return requireOk(deviceFeignClient.getDevice(id));
    }

    public List<AlarmRecordViewDto> listAlarmRecords(Long ruleId) {
        return requireOk(alarmRecordFeignClient.listRecords(ruleId));
    }

    public AlarmRecordViewDto getAlarmRecord(Long id) {
        return requireOk(alarmRecordFeignClient.getRecord(id));
    }

    private static <T> T requireOk(ApiResult<T> body) {
        if (body == null) {
            throw new IllegalStateException("下游服务返回空响应");
        }
        if (body.getCode() != 0) {
            throw new IllegalStateException("下游业务失败: " + body.getMessage());
        }
        return body.getData();
    }
}
