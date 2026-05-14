package com.iot.platform.alarm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "iot.alarm")
public class IotAlarmProperties {

    /** 规则评估周期（毫秒） */
    private long evaluateIntervalMs = 10000;

    /** Redis 中保存规则状态的前缀 */
    private String stateKeyPrefix = "alarm:rule:";

    /**
     * 设备元数据在 Redis 中的键前缀（由 iot-service-device 写入）。
     */
    private String deviceMetaKeyPrefix = "iot:device:meta:";

    /**
     * 请求未带遥测 Redis 键时的拼接模板。
     * 支持占位符：{deviceId} {deviceTypeId} {pointCode} {externalPointId}
     */
    private String telemetryKeyTemplate = "telemetry:{deviceId}:{pointCode}";

    public long getEvaluateIntervalMs() {
        return evaluateIntervalMs;
    }

    public void setEvaluateIntervalMs(long evaluateIntervalMs) {
        this.evaluateIntervalMs = evaluateIntervalMs;
    }

    public String getStateKeyPrefix() {
        return stateKeyPrefix;
    }

    public void setStateKeyPrefix(String stateKeyPrefix) {
        this.stateKeyPrefix = stateKeyPrefix;
    }

    public String getDeviceMetaKeyPrefix() {
        return deviceMetaKeyPrefix;
    }

    public void setDeviceMetaKeyPrefix(String deviceMetaKeyPrefix) {
        this.deviceMetaKeyPrefix = deviceMetaKeyPrefix;
    }

    public String getTelemetryKeyTemplate() {
        return telemetryKeyTemplate;
    }

    public void setTelemetryKeyTemplate(String telemetryKeyTemplate) {
        this.telemetryKeyTemplate = telemetryKeyTemplate;
    }
}
