package com.iot.platform.device.web;

import com.iot.platform.common.api.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class PingController {

    @GetMapping("/ping")
    public ApiResult<Map<String, String>> ping() {
        return ApiResult.ok(Collections.singletonMap("service", "iot-service-device"));
    }
}
