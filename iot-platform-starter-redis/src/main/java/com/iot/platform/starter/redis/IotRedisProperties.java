package com.iot.platform.starter.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 平台侧 Redis 扩展开关；连接地址、端口、密码等请使用 Spring Boot 标准配置前缀 spring.redis.*
 * （写在 application / bootstrap / Nacos 均可），由 spring-boot-starter-data-redis 创建连接。
 */
@ConfigurationProperties(prefix = "iot.middleware.redis")
public class IotRedisProperties {

    /**
     * 是否注册平台辅助 Bean（如 IotRedisHelper）。连接参数仍走 spring.redis.*。
     */
    private boolean enabled = true;

    /** 可选：公共 Helper 使用的键前缀 */
    private String keyPrefix = "";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }
}
