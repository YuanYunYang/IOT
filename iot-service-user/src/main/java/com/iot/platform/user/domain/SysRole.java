package com.iot.platform.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * RBAC 角色：通过中间表关联 SysPermission，再分配给用户。
 */
@Getter
@Setter
@Entity
@Table(name = "sys_role")
public class SysRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 角色编码，唯一，用于程序判断（如 ADMIN、OPERATOR） */
    @Column(nullable = false, unique = true, length = 64)
    private String roleCode;

    /** 角色显示名称 */
    @Column(nullable = false, length = 128)
    private String roleName;

    /** 备注说明 */
    @Column(length = 256)
    private String remark;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    /** 角色拥有的权限集合（中间表 sys_role_permission） */
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<SysPermission> permissions = new HashSet<>();
}
