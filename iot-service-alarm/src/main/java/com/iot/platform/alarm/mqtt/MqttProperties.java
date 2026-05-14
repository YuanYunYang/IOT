package com.iot.platform.alarm.mqtt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "iot.mqtt")
public class MqttProperties {

    private boolean enabled = false;

    /** Broker 地址，例如 tcp://192.168.1.100:1883 */
    private String serverUri = "tcp://127.0.0.1:1883";

    private String clientId = "iot-service-alarm";

    private String username;
    private String password;

    /** 订阅主题，支持通配符，例如 meter/+/telemetry */
    private String topic = "meter/+/telemetry";

    private int qos = 1;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getServerUri() {
        return serverUri;
    }

    public void setServerUri(String serverUri) {
        this.serverUri = serverUri;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }
}

