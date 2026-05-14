package com.iot.platform.alarm.mqtt;

import lombok.Data;

/**
 * MQTT 遥测载荷 JSON 示例：
 * {"deviceId":1,"deviceTypeId":10,"pointCode":"TEMP_REALTIME","value":28.6,"ts":1710000000000}
 */
@Data
public class MqttTelemetryMessage {
    private Long deviceId;
    private Long deviceTypeId;
    private Long pointId;
    private String pointCode;
    /** 可选；参与遥测键模板占位符 externalPointId */
    private String externalPointId;
    private Object value;
    private Long ts;
}
