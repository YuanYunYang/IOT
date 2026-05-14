package com.iot.platform.core.client.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * 与 user 服务 RoleResponse 的 JSON 字段对齐，供 Feign 反序列化。
 */
@Data
public class RoleViewDto {
    private Long id;
    private String roleCode;
    private String roleName;
    private String remark;
    private Instant createdAt;
    private List<Long> permissionIds;
}
