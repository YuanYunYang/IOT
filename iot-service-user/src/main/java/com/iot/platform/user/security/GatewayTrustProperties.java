package com.iot.platform.user.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 下游服务是否仅接受经 iot-gateway 校验后转发的请求。
 */
@Data
@ConfigurationProperties(prefix = "iot.gateway-trust")
public class GatewayTrustProperties {

    /** 是否启用网关头校验 */
    private boolean enabled = false;

    /** 与网关注入的 X-Gateway-Secret 一致的共享密钥 */
    private String secret = "";

    /** 不校验网关头的白名单路径（如健康检查等） */
    private List<String> permitPaths = defaultPermitPaths();

    private static List<String> defaultPermitPaths() {
        List<String> p = new ArrayList<>();
        p.add("/ping");
        p.add("/actuator/health");
        p.add("/actuator/info");
        p.add("/error");
        p.add("/api/v1/auth/login");
        return p;
    }
}

