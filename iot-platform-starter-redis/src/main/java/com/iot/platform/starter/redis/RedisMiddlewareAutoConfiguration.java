package com.iot.platform.starter.redis;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 绑定 IotRedisProperties；使用 Spring 提供的 StringRedisTemplate / RedisTemplate（来自 spring-boot-starter-data-redis），
 * 版本可在本 starter 模块统一约束。
 */
@Configuration
@ConditionalOnClass(RedisConnectionFactory.class)
@ConditionalOnProperty(prefix = "iot.middleware.redis", name = "enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties(IotRedisProperties.class)
public class RedisMiddlewareAutoConfiguration {

    @Bean
    @ConditionalOnBean(StringRedisTemplate.class)
    public IotRedisHelper iotRedisHelper(StringRedisTemplate stringRedisTemplate) {
        return new IotRedisHelper(stringRedisTemplate);
    }
}
