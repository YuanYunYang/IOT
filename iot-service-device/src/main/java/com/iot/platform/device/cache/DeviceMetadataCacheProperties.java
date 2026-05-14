package com.iot.platform.device.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "iot.device.metadata-cache")
public class DeviceMetadataCacheProperties {

    /**
     * 是否将设备类型/点位元数据放入 Redis，并在增删改后同步。
     */
    private boolean enabled = true;

    /**
     * 元数据在 Redis 中的键前缀。
     */
    private String keyPrefix = "iot:device:meta:";

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

