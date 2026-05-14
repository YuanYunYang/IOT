package com.iot.platform.user.web;

import com.iot.platform.common.api.ApiResult;
import com.iot.platform.user.dto.AuthProfile;
import com.iot.platform.user.dto.DefaultStoreRequest;
import com.iot.platform.user.dto.LoginRequest;
import com.iot.platform.user.dto.LoginResponse;
import com.iot.platform.user.dto.SwitchStoreRequest;
import com.iot.platform.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "认证", description = "登录、切换门店、会话上下文；JWT 声明 tid/ttp/sid 见 iot-platform-common JwtClaimKeys")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "登录", description = "校验租户编码、用户名密码后签发 JWT；请在后续请求携带 Bearer。响应 profile 与 Claims 中 tid、ttp、sid 一致。")
    public ApiResult<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResult.ok(authService.login(request));
    }

    @PostMapping("/switch-store")
    @Operation(summary = "切换当前门店", description = "校验权限后签发新 JWT（更新 sid）。需 Bearer。")
    public ApiResult<LoginResponse> switchStore(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody SwitchStoreRequest request) {
        return ApiResult.ok(authService.switchStore(authorization, request.getStoreId()));
    }

    @GetMapping("/session")
    @Operation(summary = "当前会话", description = "解析 Bearer JWT，返回租户与门店上下文（不做签发）。需 Bearer。")
    public ApiResult<AuthProfile> session(@RequestHeader("Authorization") String authorization) {
        return ApiResult.ok(authService.session(authorization));
    }

    @PutMapping("/default-store")
    @Operation(summary = "默认登录门店", description = "持久化默认门店；不强制刷新 JWT，下次登录优先使用该门店。需 Bearer。")
    public ApiResult<AuthProfile> updateDefaultStore(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody DefaultStoreRequest request) {
        return ApiResult.ok(authService.updateDefaultStore(authorization, request));
    }
}
