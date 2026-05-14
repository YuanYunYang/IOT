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
import java.time.Instant;

/**
 * 菜单/按钮/API 权限节点，树形结构由父级 parentId 串联。
 */
@Getter
@Setter
@Entity
@Table(name = "sys_permission")
public class SysPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 父级权限 id，顶级为 null */
    private Long parentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PermType permType;

    /** 唯一编码，如 device:list、device:btn:add */
    @Column(nullable = false, unique = true, length = 128)
    private String permCode;

    @Column(nullable = false, length = 128)
    private String name;

    /** 前端路由或后端资源路径；API 型权限可与 httpMethod 字段组合使用 */
    @Column(length = 256)
    private String path;

    /** 绑定 HTTP 方法时使用，如 GET、POST */
    @Column(length = 16)
    private String httpMethod;

    /** 同级排序，越小越靠前 */
    @Column(nullable = false)
    private Integer sortOrder = 0;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();
}
