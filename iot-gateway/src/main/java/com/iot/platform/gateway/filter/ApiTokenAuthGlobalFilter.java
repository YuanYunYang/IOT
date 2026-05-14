package com.iot.platform.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.platform.common.api.ApiResult;
import com.iot.platform.common.api.PlatformErrorCode;
import com.iot.platform.common.security.GatewayHeaders;
import com.iot.platform.common.security.jwt.JwtCodec;
import com.iot.platform.gateway.config.GatewaySecurityProperties;
import io.jsonwebtoken.JwtException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * 仅在网关校验外部 HTTP 头 Authorization: Bearer token；通过后向下游注入内部头，
 * 下游通过 X-Gateway-Verified 与 X-Gateway-Secret 识别合法转发。
 */
@Component
public class ApiTokenAuthGlobalFilter implements GlobalFilter, Ordered {

    private static final String BEARER_PREFIX = "Bearer ";

    private final GatewaySecurityProperties properties;
    private final ObjectMapper objectMapper;

    public ApiTokenAuthGlobalFilter(GatewaySecurityProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!properties.isEnabled()) {
            return chain.filter(exchange);
        }

        String path = exchange.getRequest().getURI().getRawPath();
        if (path == null || path.isEmpty()) {
            path = "/";
        }
        if (isPermitted(path)) {
            return chain.filter(exchange);
        }

        GatewaySecurityProperties.Jwt jwtConf = properties.getJwt();
        boolean jwtOn = jwtConf != null && jwtConf.isEnabled() && StringUtils.hasText(jwtConf.getSecret());
        Set<String> allowedStatic = normalize(properties.getTokens());
        if (!jwtOn && allowedStatic.isEmpty()) {
            return writeJson(exchange, HttpStatus.INTERNAL_SERVER_ERROR,
                    ApiResult.fail(PlatformErrorCode.INTERNAL_ERROR,
                            "网关未配置鉴权：请设置 iot.gateway.security.jwt 或 iot.gateway.security.tokens"));
        }

        String token = extractBearer(exchange.getRequest().getHeaders());
        if (token == null) {
            return writeJson(exchange, HttpStatus.UNAUTHORIZED, ApiResult.fail(PlatformErrorCode.UNAUTHORIZED));
        }

        boolean ok = false;
        if (jwtOn) {
            try {
                JwtCodec.parseAndVerify(jwtConf.getSecret(), token);
                ok = true;
            } catch (JwtException | IllegalArgumentException ignored) {
                // 继续尝试静态 token
            }
        }
        if (!ok && !allowedStatic.isEmpty() && allowedStatic.contains(token)) {
            ok = true;
        }
        if (!ok) {
            return writeJson(exchange, HttpStatus.UNAUTHORIZED, ApiResult.fail(PlatformErrorCode.UNAUTHORIZED));
        }

        // 告知下游：已通过网关鉴权，并带上共享 secret 供 GatewayTrustFilter 校验
        String downstreamSecret = properties.getDownstreamSecret();
        ServerHttpRequest mutatedReq = exchange.getRequest().mutate()
                .headers(h -> {
                    h.set(GatewayHeaders.HEADER_GATEWAY_VERIFIED, "1");
                    if (StringUtils.hasText(downstreamSecret)) {
                        h.set(GatewayHeaders.HEADER_GATEWAY_SECRET, downstreamSecret);
                    }
                })
                .build();

        return chain.filter(exchange.mutate().request(mutatedReq).build());
    }

    private boolean isPermitted(String path) {
        for (String p : properties.getPermitPaths()) {
            if (p == null || p.isEmpty()) {
                continue;
            }
            if (path.equals(p) || path.startsWith(p.endsWith("/") ? p : p + "/")) {
                return true;
            }
        }
        return false;
    }

    private static String extractBearer(HttpHeaders headers) {
        String auth = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(auth)) {
            return null;
        }
        if (auth.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            return auth.substring(BEARER_PREFIX.length()).trim();
        }
        return null;
    }

    private static Set<String> normalize(java.util.List<String> tokens) {
        Set<String> out = new HashSet<>();
        if (tokens == null) {
            return out;
        }
        for (String t : tokens) {
            if (t != null && !t.trim().isEmpty()) {
                out.add(t.trim());
            }
        }
        return out;
    }

    private Mono<Void> writeJson(ServerWebExchange exchange, HttpStatus status, ApiResult<?> body) {
        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (Exception e) {
            bytes = ("{\"success\":false,\"code\":" + status.value() + ",\"message\":\"" + status.getReasonPhrase() + "\"}")
                    .getBytes(StandardCharsets.UTF_8);
        }
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }

    @Override
    public int getOrder() {
        // 顺序：请求 ID 之后、访问日志所在过滤器之前
        return Ordered.HIGHEST_PRECEDENCE + 5;
    }
}

