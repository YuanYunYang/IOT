package com.iot.platform.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * 生成或透传请求 ID，便于下游服务链路关联。
 */
@Component
public class RequestIdGlobalFilter implements GlobalFilter, Ordered {

    public static final String HEADER_REQUEST_ID = "X-Request-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String reqId = headers.getFirst(HEADER_REQUEST_ID);
        if (reqId == null || reqId.trim().isEmpty()) {
            reqId = UUID.randomUUID().toString().replace("-", "");
        }
        final String finalReqId = reqId;
        exchange.getResponse().getHeaders().set(HEADER_REQUEST_ID, finalReqId);
        ServerWebExchange mutated = exchange.mutate()
                .request(r -> r.headers(h -> h.set(HEADER_REQUEST_ID, finalReqId)))
                .build();
        return chain.filter(mutated);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

