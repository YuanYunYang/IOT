package com.iot.platform.starter.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 示例消费者；配置 {@code iot.middleware.kafka.examples-enabled=true} 时启用。
 */
@Component
@ConditionalOnProperty(prefix = "iot.middleware.kafka", name = "examples-enabled", havingValue = "true")
public class IotKafkaExampleConsumer {

    private static final Logger log = LoggerFactory.getLogger(IotKafkaExampleConsumer.class);

    @KafkaListener(topics = "${iot.middleware.kafka.example-topic:iot.demo.topic}")
    public void onMessage(String body) {
        log.info("Kafka demo topic message: body={}", body);
    }
}
