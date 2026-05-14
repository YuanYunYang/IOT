package com.iot.platform.core.client;

import com.iot.platform.common.api.ApiResult;
import com.iot.platform.core.client.dto.RoleViewDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "iot-service-user", contextId = "roleAdminFeign", path = "/api/v1/roles")
public interface RoleAdminFeignClient {

    @GetMapping
    ApiResult<List<RoleViewDto>> listRoles();

    @GetMapping("/{id}")
    ApiResult<RoleViewDto> getRole(@PathVariable("id") Long id);
}
