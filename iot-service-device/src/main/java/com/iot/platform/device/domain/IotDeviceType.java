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

@Getter
@Setter
@Entity
@Table(name = "device_type")
public class IotDeviceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 类型编码（唯一），如 TEMP_SENSOR */
    @Column(name = "type_code", nullable = false, unique = true, length = 64)
    private String typeCode;

    /** 类型名称，如 温度传感器 */
    @Column(name = "type_name", nullable = false, length = 128)
    private String typeName;

    /** 子系统，如 环境传感器 */
    @Column(length = 128)
    private String subsystem;

    @Column(length = 256)
    private String remark;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    private Instant updatedAt;
}

