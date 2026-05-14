package com.iot.platform.starter.clickhouse;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "iot.clickhouse")
public class IotClickHouseProperties {

    private boolean enabled = true;

    /**
     * JDBC URL（在 iot.clickhouse.enabled=true 时必填），例如 jdbc:ch:http://host:8123/default。
     */
    private String url;

    /** 用户名；未配置时按空字符串传给驱动。 */
    private String username;

    /** 密码；未配置时按空字符串传给驱动。 */
    private String password;

    /** Socket 超时（毫秒）。 */
    private int socketTimeoutMs = 60000;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public int getSocketTimeoutMs() {
        return socketTimeoutMs;
    }

    public void setSocketTimeoutMs(int socketTimeoutMs) {
        this.socketTimeoutMs = socketTimeoutMs;
    }
}
