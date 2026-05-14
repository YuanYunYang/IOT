package com.iot.platform.user.web;

import com.iot.platform.common.api.ApiResult;
import com.iot.platform.user.dto.UserCreateRequest;
import com.iot.platform.user.dto.UserResponse;
import com.iot.platform.user.dto.UserUpdateRequest;
import com.iot.platform.user.service.UserAdminService;
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
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "系统用户的查询、创建、更新、删除及角色分配")
public class UserAdminController {

    private final UserAdminService userAdminService;

    @GetMapping
    @Operation(summary = "用户列表", description = "返回全部用户及其角色编码列表")
    public ApiResult<List<UserResponse>> list() {
        return ApiResult.ok(userAdminService.listAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "用户详情", description = "根据主键查询单个用户")
    public ApiResult<UserResponse> get(
            @Parameter(description = "用户 ID", required = true) @PathVariable Long id) {
        return ApiResult.ok(userAdminService.get(id));
    }

    @PostMapping
    @Operation(summary = "创建用户", description = "新建用户并可选分配角色")
    public ApiResult<UserResponse> create(@Valid @RequestBody UserCreateRequest req) {
        return ApiResult.ok(userAdminService.create(req));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户", description = "更新基本信息、密码或角色；未传字段不修改")
    public ApiResult<UserResponse> update(
            @Parameter(description = "用户 ID", required = true) @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest req) {
        return ApiResult.ok(userAdminService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "按主键删除用户")
    public ApiResult<Void> delete(
            @Parameter(description = "用户 ID", required = true) @PathVariable Long id) {
        userAdminService.delete(id);
        return ApiResult.ok(null);
    }
}
