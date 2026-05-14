package com.iot.platform.user.web;

import com.iot.platform.common.api.ApiResult;
import com.iot.platform.user.dto.TenantCreateRequest;
import com.iot.platform.user.dto.TenantResponse;
import com.iot.platform.user.service.TenantAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
@Tag(name = "租户管理", description = "SaaS 租户（企业多门店 / 单门店）")
public class TenantAdminController {

    private final TenantAdminService tenantAdminService;

    @GetMapping
    @Operation(summary = "租户列表")
    public ApiResult<List<TenantResponse>> list() {
        return ApiResult.ok(tenantAdminService.listAll());
    }

    @PostMapping
    @Operation(summary = "创建租户")
    public ApiResult<TenantResponse> create(@Valid @RequestBody TenantCreateRequest req) {
        return ApiResult.ok(tenantAdminService.create(req));
    }
}
