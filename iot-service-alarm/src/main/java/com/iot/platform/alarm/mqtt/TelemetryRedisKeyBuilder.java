package com.iot.platform.alarm.mqtt;

import com.iot.platform.alarm.config.IotAlarmProperties;
import com.iot.platform.common.util.EmptyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 按与报警规则相同的模板拼接遥测 Redis 键，占位符含 deviceId、deviceTypeId、pointCode、externalPointId 等。
 */
@Component
@RequiredArgsConstructor
public class TelemetryRedisKeyBuilder {

    private final IotAlarmProperties alarmProperties;

    public String build(Long deviceId, Long deviceTypeId, String pointCode, String externalPointId) {
        String tpl = alarmProperties.getTelemetryKeyTemplate();
        if (EmptyUtil.isBlank(tpl)) {
            tpl = "telemetry:{deviceId}:{pointCode}";
        }
        String did = deviceId == null ? "" : String.valueOf(deviceId);
        String tid = deviceTypeId == null ? "" : String.valueOf(deviceTypeId);
        String pc = EmptyUtil.isBlank(pointCode) ? "" : pointCode;
        String ext = externalPointId == null ? "" : externalPointId;
        return tpl
                .replace("{deviceId}", did)
                .replace("{deviceTypeId}", tid)
                .replace("{pointCode}", pc)
                .replace("{externalPointId}", ext);
    }
}
