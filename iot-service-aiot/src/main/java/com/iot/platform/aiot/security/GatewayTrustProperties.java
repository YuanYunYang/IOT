package com.iot.platform.aiot.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 下游微服务是否只信任「经 iot-gateway 转发」的请求：与网关注入的 X-Gateway-* 请求头及本地密钥对齐。
 */
@Data
@ConfigurationProperties(prefix = "iot.gateway-trust")
public class GatewayTrustProperties {

    /** 为 false 时不校验，便于本地直连调试 */
    private boolean enabled = false;

    /** 与网关配置项 iot.gateway.security.downstream-secret 一致，用于比对请求头 */
    private String secret = "";

    /** 白名单路径（如健康检查）不校验网关头 */
    private List<String> permitPaths = defaultPermitPaths();

    private static List<String> defaultPermitPaths() {
        List<String> p = new ArrayList<>();
        p.add("/ping");
        p.add("/actuator/health");
        p.add("/actuator/info");
        p.add("/error");
        return p;
    }
}

