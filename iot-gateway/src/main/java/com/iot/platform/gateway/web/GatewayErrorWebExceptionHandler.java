package com.iot.platform.gateway.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.platform.common.api.ApiResult;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 将网关异常转换为统一 JSON 响应体。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GatewayErrorWebExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GatewayErrorWebExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String msg = "Gateway error";
        if (ex instanceof ResponseStatusException) {
            ResponseStatusException rse = (ResponseStatusException) ex;
            status = rse.getStatus();
            msg = rse.getReason() != null ? rse.getReason() : rse.getMessage();
        } else if (ex != null && ex.getMessage() != null && !ex.getMessage().trim().isEmpty()) {
            msg = ex.getMessage();
        }

        ApiResult<Void> body = ApiResult.fail(status.value(), msg);
        byte[] bytes = toJsonBytes(body);

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().getHeaders().set(HttpHeadersExt.HEADER_GATEWAY_ERROR, "1");

        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(bytes)));
    }

    private byte[] toJsonBytes(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            return ("{\"code\":500,\"message\":\"Gateway error\",\"data\":null}")
                    .getBytes(StandardCharsets.UTF_8);
        }
    }

    /**
     * 避免额外依赖；响应头常量集中在本嵌套类。
     */
    private static final class HttpHeadersExt {
        private HttpHeadersExt() {
        }

        private static final String HEADER_GATEWAY_ERROR = "X-Gateway-Error";
    }
}

