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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.Instant;

/**
 * 门店：隶属于租户；向量库等数据按 tenant + store 隔离。
 */
@Getter
@Setter
@Entity
@Table(name = "sys_store", uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "store_code"}))
public class SysStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private SysTenant tenant;

    /** 租户内唯一门店编码 */
    @Column(name = "store_code", nullable = false, length = 64)
    private String storeCode;

    @Column(name = "store_name", nullable = false, length = 128)
    private String storeName;

    @Column(nullable = false)
    private Boolean enabled = true;

    /** 列表排序，越小越靠前 */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    private Instant updatedAt;
}
