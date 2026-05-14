package com.iot.platform.user.web;

import com.iot.platform.common.api.ApiResult;
import com.iot.platform.user.dto.RoleRequest;
import com.iot.platform.user.dto.RoleResponse;
import com.iot.platform.user.service.RoleAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "角色的增删改查及权限绑定")
public class RoleAdminController {

    private final RoleAdminService roleAdminService;

    @GetMapping
    @Operation(summary = "角色列表", description = "返回全部角色")
    public ApiResult<List<RoleResponse>> list() {
        return ApiResult.ok(roleAdminService.listAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "角色详情", description = "根据主键查询角色")
    public ApiResult<RoleResponse> get(
            @Parameter(description = "角色 ID", required = true) @PathVariable Long id) {
        return ApiResult.ok(roleAdminService.get(id));
    }

    @PostMapping
    @Operation(summary = "创建角色", description = "新建角色并可选绑定权限")
    public ApiResult<RoleResponse> create(@Valid @RequestBody RoleRequest req) {
        return ApiResult.ok(roleAdminService.create(req));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新角色", description = "更新角色信息与权限绑定")
    public ApiResult<RoleResponse> update(
            @Parameter(description = "角色 ID", required = true) @PathVariable Long id,
            @Valid @RequestBody RoleRequest req) {
        return ApiResult.ok(roleAdminService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色", description = "按主键删除角色")
    public ApiResult<Void> delete(
            @Parameter(description = "角色 ID", required = true) @PathVariable Long id) {
        roleAdminService.delete(id);
        return ApiResult.ok(null);
    }
}
