package com.iot.platform.aiot.config;

import com.iot.platform.aiot.security.GatewayTrustProperties;
import com.iot.platform.common.security.GatewayHeaders;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
