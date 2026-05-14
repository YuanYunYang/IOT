package com.iot.platform.starter.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 平台 Kafka 开关与示例 Topic；集群地址请使用 {@code spring.kafka.bootstrap-servers}。
 */
@ConfigurationProperties(prefix = "iot.middleware.kafka")
public class IotKafkaProperties {

    private boolean enabled = true;

    /** 开启后注册示例 {@link org.springframework.kafka.annotation.KafkaListener}（仅演示）。 */
    private boolean examplesEnabled = false;

    /** 示例监听 Topic */
    private String exampleTopic = "iot.demo.topic";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isExamplesEnabled() {
        return examplesEnabled;
    }

    public void setExamplesEnabled(boolean examplesEnabled) {
        this.examplesEnabled = examplesEnabled;
    }

    public String getExampleTopic() {
        return exampleTopic;
    }

    public void setExampleTopic(String exampleTopic) {
        this.exampleTopic = exampleTopic;
    }
}
