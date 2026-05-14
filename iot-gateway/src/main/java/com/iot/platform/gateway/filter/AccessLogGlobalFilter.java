package com.iot.platform.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 轻量访问日志：记录耗时与 HTTP 状态。
 */
@Component
public class AccessLogGlobalFilter implements GlobalFilter, Ordered {



    private static final Logger log = LoggerFactory.getLogger(AccessLogGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long start = System.currentTimeMillis();
        ServerHttpRequest req = exchange.getRequest();
        String method = req.getMethodValue();
        String path = req.getURI().getRawPath();
        String reqId = req.getHeaders().getFirst(RequestIdGlobalFilter.HEADER_REQUEST_ID);

        return chain.filter(exchange)
                .doFinally(signalType -> {
                    ServerHttpResponse resp = exchange.getResponse();
                    Integer status = resp.getStatusCode() != null ? resp.getStatusCode().value() : null;
                    long cost = System.currentTimeMillis() - start;
                    if (reqId == null || reqId.trim().isEmpty()) {
                        log.info("{} {} status={} costMs={}", method, path, status, cost);
                    } else {
                        log.info("{} {} status={} costMs={} reqId={}", method, path, status, cost, reqId);
                    }
                });
    }

    @Override
    public int getOrder() {
        // 顺序：在请求 ID 过滤器之后执行
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}

