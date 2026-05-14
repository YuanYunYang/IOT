package com.iot.platform.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关专用鉴权配置；外部请求须在网关完成令牌校验。
 */
@ConfigurationProperties(prefix = "iot.gateway.security")
public class GatewaySecurityProperties {

    /** 是否启用网关鉴权 */
    private boolean enabled = false;

    /**
     * 允许的 API 令牌列表（演示模式）；生产可改为对接用户服务或 IAM。任一匹配即通过。
     */
    private List<String> tokens = new ArrayList<>();

    /** 无需鉴权的路径前缀（如健康检查、登录等） */
    private List<String> permitPaths = defaultPermitPaths();

    /**
     * 写入下游请求头的共享密钥；下游比对以防止绕过网关直连。
     */
    private String downstreamSecret = "";

    private Jwt jwt = new Jwt();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public List<String> getPermitPaths() {
        return permitPaths;
    }

    public void setPermitPaths(List<String> permitPaths) {
        this.permitPaths = permitPaths;
    }

    public String getDownstreamSecret() {
        return downstreamSecret;
    }

    public void setDownstreamSecret(String downstreamSecret) {
        this.downstreamSecret = downstreamSecret;
    }

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    public static class Jwt {

        /** 是否启用 JWT 校验（与静态 tokens 可同时存在，任一通过即可）。 */
        private boolean enabled = false;

        /** 与用户服务 iot.security.jwt.secret 保持一致，至少 32 字节更安全。 */
        private String secret = "";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }

    private static List<String> defaultPermitPaths() {
        List<String> p = new ArrayList<>();
        p.add("/actuator/health");
        p.add("/actuator/info");
        p.add("/error");
        p.add("/api/v1/user/auth/login");
        return p;
    }
}

