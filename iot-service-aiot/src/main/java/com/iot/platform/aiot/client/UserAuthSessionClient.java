package com.iot.platform.aiot.client;

import com.iot.platform.aiot.kb.dto.AuthProfileDto;
import com.iot.platform.common.api.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 拉取当前登录用户的租户与可访问门店（与 JWT 一致），用于知识库检索范围裁剪。
 */
@FeignClient(name = "iot-service-user", contextId = "userAuthSessionFeign", path = "/")
public interface UserAuthSessionClient {

    @GetMapping("/api/v1/auth/session")
    ApiResult<AuthProfileDto> session(@RequestHeader("Authorization") String authorization);
}
