package com.iot.platform.core.client.dto;

import lombok.Data;

import java.time.Instant;

/**
 * 与报警服务的 AlarmRecord 实体 JSON 字段对齐（无 JPA 注解），供 Feign 反序列化。
 */
@Data
public class AlarmRecordViewDto {
    private Long id;
    private Long ruleId;
    private Long deviceId;
    private String title;
    private String message;
    private String alarmLevel;
    private String pointValueSnapshot;
    private Instant triggeredAt;
    private Boolean acknowledged;
}
