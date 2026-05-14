package com.iot.platform.common.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * 各微服务与网关共用的 JWT 配置：签发（用户服务）、校验（网关与其它服务 Filter）。
 */
@ConfigurationProperties(prefix = "iot.security.jwt")
public class JwtSecurityProperties {

    /** HS256 密钥，须与网关 {@code iot.gateway.security.jwt.secret} 一致。 */
    private String secret = "";

    /** 访问令牌有效期（用户服务登录签发时使用），默认 8 小时。 */
    private Duration accessTokenTtl = Duration.ofHours(8);

    /**
     * 是否在服务侧校验 {@code Authorization: Bearer} JWT。
     * 为 true 时，除白名单路径外须携带有效 JWT，或与 {@link com.iot.platform.common.security.GatewayHeaders} 网关头同时满足其一。
     */
    private boolean validateBearer = true;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Duration getAccessTokenTtl() {
        return accessTokenTtl;
    }

    public void setAccessTokenTtl(Duration accessTokenTtl) {
        this.accessTokenTtl = accessTokenTtl;
    }

    public boolean isValidateBearer() {
        return validateBearer;
    }

    public void setValidateBearer(boolean validateBearer) {
        this.validateBearer = validateBearer;
    }
}
