package com.iot.platform.user.web;

import com.iot.platform.common.api.ApiResult;
import com.iot.platform.user.dto.PermissionRequest;
import com.iot.platform.user.dto.PermissionResponse;
import com.iot.platform.user.service.PermissionAdminService;
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
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Tag(name = "权限管理", description = "权限资源的扁平列表、树形结构与增删改查")
public class PermissionAdminController {

    private final PermissionAdminService permissionAdminService;

    @GetMapping
    @Operation(summary = "权限扁平列表", description = "返回全部权限节点（非树形）")
    public ApiResult<List<PermissionResponse>> listFlat() {
        return ApiResult.ok(permissionAdminService.listFlat());
    }

    @GetMapping("/tree")
    @Operation(summary = "权限树", description = "返回按父子关系嵌套的权限树")
    public ApiResult<List<PermissionResponse>> tree() {
        return ApiResult.ok(permissionAdminService.tree());
    }

    @GetMapping("/{id}")
    @Operation(summary = "权限详情", description = "根据主键查询单个权限节点")
    public ApiResult<PermissionResponse> get(
            @Parameter(description = "权限 ID", required = true) @PathVariable Long id) {
        return ApiResult.ok(permissionAdminService.get(id));
    }

    @PostMapping
    @Operation(summary = "创建权限", description = "新增菜单/页面/按钮权限节点")
    public ApiResult<PermissionResponse> create(@Valid @RequestBody PermissionRequest req) {
        return ApiResult.ok(permissionAdminService.create(req));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新权限", description = "更新权限节点信息")
    public ApiResult<PermissionResponse> update(
            @Parameter(description = "权限 ID", required = true) @PathVariable Long id,
            @Valid @RequestBody PermissionRequest req) {
        return ApiResult.ok(permissionAdminService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除权限", description = "按主键删除权限节点")
    public ApiResult<Void> delete(
            @Parameter(description = "权限 ID", required = true) @PathVariable Long id) {
        permissionAdminService.delete(id);
        return ApiResult.ok(null);
    }
}
