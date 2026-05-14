package com.iot.platform.user.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.Instant;

/**
 * SaaS 租户（企业或单店组织的计费与隔离边界）。
 */
@Getter
@Setter
@Entity
@Table(name = "sys_tenant", uniqueConstraints = @UniqueConstraint(columnNames = "tenant_code"))
public class SysTenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 租户编码，登录、API 与向量库路由常用唯一标识 */
    @Column(name = "tenant_code", nullable = false, unique = true, length = 64)
    private String tenantCode;

    @Column(name = "tenant_name", nullable = false, length = 128)
    private String tenantName;

    @Enumerated(EnumType.STRING)
    @Column(name = "tenant_type", nullable = false, length = 32)
    private TenantType tenantType;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    private Instant updatedAt;
}
