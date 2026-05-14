package com.iot.platform.alarm.domain;

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
 * 用户自定义报警规则：设备类型/设备/点位 + 比较符 + 阈值 + 持续时长；实时值从 Redis 键 telemetryRedisKey 读取。
 */
@Getter
@Setter
@Entity
@Table(name = "alarm_rule")
public class AlarmRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 256)
    private String name;

    @Column(nullable = false)
    private Boolean enabled = true;

    /** 子系统，如：环境传感器 */
    @Column(length = 128)
    private String subsystem;

    @Column(name = "device_type_id")
    private Long deviceTypeId;

    @Column(name = "device_type_name", length = 128)
    private String deviceTypeName;

    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "device_name", length = 256)
    private String deviceName;

    /** 点位编码，如：实时温度 */
    @Column(name = "point_code", length = 128)
    private String pointCode;

    /** 点位定义ID（从设备服务点位表选择），可选 */
    @Column(name = "point_id")
    private Long pointId;

    /** 点位名称冗余（便于展示），可选 */
    @Column(name = "point_name", length = 128)
    private String pointName;

    @Enumerated(EnumType.STRING)
    @Column(name = "compare_operator", nullable = false, length = 8)
    private CompareOperator compareOperator;

    /** 阈值，可与点位值同量纲（如 28 表示 28℃） */
    @Column(name = "threshold_value", nullable = false, length = 64)
    private String thresholdValue;

    /** 条件需持续满足的秒数，例如 300=5 分钟 */
    @Column(name = "duration_seconds", nullable = false)
    private Integer durationSeconds = 0;

    /**
     * Redis 中存放该点位当前值的完整 key（由采集/网关写入）。
     * 示例：S.{gc}.{type}.{deviceId}.{paramId}
     */
    @Column(name = "telemetry_redis_key", nullable = false, length = 512)
    private String telemetryRedisKey;

    @Column(name = "alarm_title", length = 256)
    private String alarmTitle;

    @Column(name = "alarm_level", length = 32)
    private String alarmLevel = "WARN";

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    private Instant updatedAt;
}
