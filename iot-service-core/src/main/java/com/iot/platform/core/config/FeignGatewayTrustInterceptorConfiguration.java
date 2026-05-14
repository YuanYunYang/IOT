package com.iot.platform.core.config;

import com.iot.platform.common.security.GatewayHeaders;
import com.iot.platform.core.security.GatewayTrustProperties;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 服务间 Feign 调用自动带上网关头，供下游 {@link com.iot.platform.core.security.GatewayTrustFilter} 识别（与登录 JWT 二选一）。
 */
@Configuration
public class FeignGatewayTrustInterceptorConfiguration {

    @Bean
    public RequestInterceptor gatewayTrustOutgoingFeignInterceptor(GatewayTrustProperties gatewayTrustProperties) {
        return template -> {
            if (!gatewayTrustProperties.isEnabled()) {
                return;
            }
            String secret = gatewayTrustProperties.getSecret();
            if (secret == null || secret.trim().isEmpty()) {
                return;
            }
            template.header(GatewayHeaders.HEADER_GATEWAY_VERIFIED, "1");
            template.header(GatewayHeaders.HEADER_GATEWAY_SECRET, secret.trim());
        };
    }
}
