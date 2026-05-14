package com.iot.platform.user.web;

import com.iot.platform.common.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@Tag(name = "健康检查", description = "服务存活探测")
public class PingController {

    @GetMapping("/ping")
    @Operation(summary = "存活探测", description = "返回当前服务名称，用于网关或负载均衡健康检查")
    public ApiResult<Map<String, String>> ping() {
        return ApiResult.ok(Collections.singletonMap("service", "iot-service-user"));
    }
}
