package com.iot.platform.core.web;

import com.iot.platform.common.api.ApiResult;
import com.iot.platform.common.api.PlatformErrorCode;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CoreRestExceptionHandler {

    @ExceptionHandler(FeignException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ApiResult<Void> onFeign(FeignException ex) {
        return ApiResult.fail(PlatformErrorCode.UPSTREAM_ERROR,
                "调用下游服务失败: HTTP " + ex.status());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ApiResult<Void> onIllegalState(IllegalStateException ex) {
        return ApiResult.fail(PlatformErrorCode.UPSTREAM_ERROR, ex.getMessage());
    }
}
