package com.iot.platform.core.client;

import com.iot.platform.common.api.ApiResult;
import com.iot.platform.core.client.dto.UserViewDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "iot-service-user", contextId = "userAdminFeign", path = "/api/v1/users")
public interface UserAdminFeignClient {

    @GetMapping
    ApiResult<List<UserViewDto>> listUsers();

    @GetMapping("/{id}")
    ApiResult<UserViewDto> getUser(@PathVariable("id") Long id);
}
