package com.iot.platform.aiot.client;

import com.iot.platform.common.api.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * 示例 Feign：按 Nacos 服务名调用对端（可走网关 lb:// 或直接发现）。
 */
@FeignClient(name = "iot-service-user", contextId = "userPingFeign", path = "/")
public interface UserServicePingClient {

    @GetMapping("/ping")
    ApiResult<Map<String, String>> ping();
}
