package com.iot.platform.starter.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Objects;

/**
 * 基于 {@link KafkaTemplate} 的轻量封装；建议在配置中使用 String 序列化。
 */
public class IotKafkaHelper {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public IotKafkaHelper(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = Objects.requireNonNull(kafkaTemplate, "kafkaTemplate");
    }

    public ListenableFuture<?> send(String topic, String payload) {
        return kafkaTemplate.send(topic, payload);
    }

    public ListenableFuture<?> send(String topic, String key, String payload) {
        return kafkaTemplate.send(topic, key, payload);
    }

    public KafkaTemplate<String, String> getKafkaTemplate() {
        return kafkaTemplate;
    }
}
