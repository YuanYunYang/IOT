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
 * 物联网设备实例：归属某设备类型 IotDeviceType，业务上可通过 externalDeviceId 与外部系统对齐。
 */
@Getter
@Setter
@Entity
@Table(name = "device")
public class IotDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 子系统，如：环境传感器 */
    @Column(length = 128)
    private String subsystem;

    @Column(name = "device_type_id")
    private Long deviceTypeId;

    @Column(name = "device_name", nullable = false, length = 256)
    private String deviceName;

    @Column(length = 256)
    private String location;

    /** 外部/业务设备编号 */
    @Column(name = "external_device_id", length = 128)
    private String externalDeviceId;

    @Column(length = 512)
    private String remark;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    private Instant updatedAt;
}
