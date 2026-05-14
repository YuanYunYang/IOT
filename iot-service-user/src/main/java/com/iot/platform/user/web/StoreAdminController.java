package com.iot.platform.user.web;

import com.iot.platform.common.api.ApiResult;
import com.iot.platform.user.dto.StoreBrief;
import com.iot.platform.user.dto.StoreCreateRequest;
import com.iot.platform.user.service.StoreAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tenants/{tenantId}/stores")
@RequiredArgsConstructor
@Tag(name = "门店管理", description = "租户下门店；单门店租户仅允许一条")
public class StoreAdminController {

    private final StoreAdminService storeAdminService;

    @GetMapping
    @Operation(summary = "门店列表")
    public ApiResult<List<StoreBrief>> list(
            @Parameter(description = "租户 ID") @PathVariable Long tenantId) {
        return ApiResult.ok(storeAdminService.listByTenant(tenantId));
    }

    @PostMapping
    @Operation(summary = "创建门店")
    public ApiResult<StoreBrief> create(
            @Parameter(description = "租户 ID") @PathVariable Long tenantId,
            @Valid @RequestBody StoreCreateRequest req) {
        return ApiResult.ok(storeAdminService.create(tenantId, req));
    }
}
