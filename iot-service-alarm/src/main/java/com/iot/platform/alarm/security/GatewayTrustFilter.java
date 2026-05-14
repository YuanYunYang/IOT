package com.iot.platform.alarm.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.platform.common.api.ApiResult;
import com.iot.platform.common.api.PlatformErrorCode;
import com.iot.platform.common.security.GatewayHeaders;
import com.iot.platform.common.security.jwt.JwtBearerTokens;
import com.iot.platform.common.security.jwt.JwtSecurityProperties;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class GatewayTrustFilter extends OncePerRequestFilter {

    private final GatewayTrustProperties gatewayTrustProperties;
    private final JwtSecurityProperties jwtSecurityProperties;
    private final ObjectMapper objectMapper;

    public GatewayTrustFilter(GatewayTrustProperties gatewayTrustProperties,
                              JwtSecurityProperties jwtSecurityProperties,
                              ObjectMapper objectMapper) {
        this.gatewayTrustProperties = gatewayTrustProperties;
        this.jwtSecurityProperties = jwtSecurityProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if (path == null || path.isEmpty()) {
            path = "/";
        }
        if (isPermitted(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!gatewayTrustProperties.isEnabled()) {
            if (jwtSecurityProperties.isValidateBearer() && StringUtils.hasText(jwtSecurityProperties.getSecret())) {
                if (JwtBearerTokens.requestHasValidBearerJwt(jwtSecurityProperties, request)) {
                    filterChain.doFilter(request, response);
                    return;
                }
                writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, ApiResult.fail(PlatformErrorCode.UNAUTHORIZED));
                return;
            }
            filterChain.doFilter(request, response);
            return;
        }

        if (JwtBearerTokens.requestHasValidBearerJwt(jwtSecurityProperties, request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String verified = request.getHeader(GatewayHeaders.HEADER_GATEWAY_VERIFIED);
        String secret = request.getHeader(GatewayHeaders.HEADER_GATEWAY_SECRET);
        if (!"1".equals(verified) || !StringUtils.hasText(secret) || !StringUtils.hasText(gatewayTrustProperties.getSecret())) {
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, ApiResult.fail(PlatformErrorCode.UNAUTHORIZED));
            return;
        }
        if (!gatewayTrustProperties.getSecret().trim().equals(secret.trim())) {
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, ApiResult.fail(PlatformErrorCode.UNAUTHORIZED));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPermitted(String path) {
        for (String p : gatewayTrustProperties.getPermitPaths()) {
            if (p == null || p.isEmpty()) {
                continue;
            }
            if (path.equals(p) || path.startsWith(p.endsWith("/") ? p : p + "/")) {
                return true;
            }
        }
        return false;
    }

    private void writeJson(HttpServletResponse response, int status, ApiResult<?> body) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
