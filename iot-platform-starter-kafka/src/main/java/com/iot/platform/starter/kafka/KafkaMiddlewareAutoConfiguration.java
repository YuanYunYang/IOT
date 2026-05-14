package com.iot.platform.starter.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * 启用 Kafka：需在配置中提供 {@code spring.kafka.bootstrap-servers} 及 String 序列化（见 Nacos 模板）。
 */
@Configuration
@EnableKafka
@EnableConfigurationProperties(IotKafkaProperties.class)
@ConditionalOnProperty(prefix = "iot.middleware.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class KafkaMiddlewareAutoConfiguration {

    @Bean
    @ConditionalOnBean(KafkaTemplate.class)
    public IotKafkaHelper iotKafkaHelper(KafkaTemplate<String, String> kafkaTemplate) {
        return new IotKafkaHelper(kafkaTemplate);
    }
}
