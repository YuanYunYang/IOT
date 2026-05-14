package com.iot.platform.device.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

/**
 * 设备类型点位定义（同一设备类型下的点位清单）。
 */
@Getter
@Setter
@Entity
@Table(name = "device_point")
public class IotDevicePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 设备类型ID */
    @Column(name = "device_type_id", nullable = false)
    private Long deviceTypeId;

    /** 点位编码（同一类型下唯一），如 TEMP_REALTIME */
    @Column(name = "point_code", nullable = false, length = 128)
    private String pointCode;

    /** 点位名称，如 实时温度 */
    @Column(name = "point_name", nullable = false, length = 128)
    private String pointName;

    /** 单位，如 ℃、%RH */
    @Column(name = "unit", length = 32)
    private String unit;

    /** 值类型，如 number/string/bool/json */
    @Column(name = "value_type", length = 32)
    private String valueType = "number";

    /** 采集参数/寄存器/业务参数ID（可选） */
    @Column(name = "external_point_id", length = 128)
    private String externalPointId;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(nullable = false)
    private Integer sortOrder = 0;

    @Column(length = 256)
    private String remark;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    private Instant updatedAt;
}

