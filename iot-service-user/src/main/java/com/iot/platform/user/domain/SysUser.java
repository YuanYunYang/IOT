package com.iot.platform.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * 平台登录用户；密码仅存哈希；用户名在租户内唯一。
 */
@Getter
@Setter
@Entity
@Table(name = "sys_user", uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "username"}))
public class SysUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 登录名，租户内唯一 */
    @Column(nullable = false, length = 64)
    private String username;

    /** 密码哈希（BCrypt 等），禁止写入日志或返回给前端 */
    @JsonIgnore
    @Column(name = "password_hash", nullable = false, length = 128)
    private String passwordHash;

    /** 展示用真实姓名，可选 */
    @Column(length = 128)
    private String realName;

    /** false 时禁止登录 */
    @Column(nullable = false)
    private Boolean enabled = true;

    /** 创建时间（UTC） */
    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    /** 最后更新时间，可选 */
    private Instant updatedAt;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private SysTenant tenant;

    /**
     * 登录默认门店（多门店用户可配置）；须在可访问门店列表内。
     * 单门店租户通常即唯一绑定门店。
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_store_id")
    private SysStore defaultStore;

    /** 用户拥有的角色（多对多，中间表 sys_user_role） */
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<SysRole> roles = new HashSet<>();

    /** 可访问门店（总部跨店 AI / 报表等多门店权限以此为准） */
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SysUserStore> storeAssignments = new HashSet<>();
}
