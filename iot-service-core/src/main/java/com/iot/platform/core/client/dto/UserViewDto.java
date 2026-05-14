package com.iot.platform.core.client.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * 与 user 服务 UserResponse 的 JSON 字段对齐，供 Feign 反序列化。
 */
@Data
public class UserViewDto {
    private Long id;
    private String username;
    private String realName;
    private Boolean enabled;
    private Instant createdAt;
    private Instant updatedAt;
    private List<String> roleCodes;
}
