package com.iot.platform.alarm.security;

import com.iot.platform.common.security.jwt.JwtSecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({GatewayTrustProperties.class, JwtSecurityProperties.class})
public class GatewayTrustConfiguration {
}

