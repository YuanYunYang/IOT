package com.iot.platform.alarm.domain;

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
 * 报警触发记录：MySQL 存业务明细；可同步到 ClickHouse 做分析。
 */
@Getter
@Setter
@Entity
@Table(name = "alarm_record")
public class AlarmRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 触发的报警规则 AlarmRule 主键 */
    @Column(name = "rule_id", nullable = false)
    private Long ruleId;

    /** 关联设备，可选（规则可能仅按类型配置） */
    @Column(name = "device_id")
    private Long deviceId;

    /** 报警标题（通常来自规则或模板） */
    @Column(name = "title", length = 256)
    private String title;

    /** 报警正文，可含阈值与当前值描述 */
    @Column(name = "message", length = 1024)
    private String message;

    /** 级别，如 WARN、CRITICAL */
    @Column(name = "alarm_level", length = 32)
    private String alarmLevel;

    /** 触发瞬间的点位值快照，便于审计 */
    @Column(name = "point_value_snapshot", length = 256)
    private String pointValueSnapshot;

    /** 触发时间（UTC） */
    @Column(name = "triggered_at", nullable = false)
    private Instant triggeredAt = Instant.now();

    /** 是否已确认/消警 */
    @Column(name = "acknowledged")
    private Boolean acknowledged = false;
}
