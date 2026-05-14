package com.iot.platform.aiot.web;

import com.iot.platform.aiot.api.AiotErrorCode;
import com.iot.platform.common.api.ApiResult;
import com.iot.platform.common.api.PlatformErrorCode;
import com.iot.platform.common.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * AIOT 统一异常转换为 ApiResult，配合全局错误码。
 */
@RestControllerAdvice
public class AiotRestExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<Void> onBusiness(BusinessException ex) {
        return ApiResult.fail(ex.getErrorCode(), ex.getDisplayMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<Void> onValidation(Exception ex) {
        String msg = ex.getMessage();
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException e = (MethodArgumentNotValidException) ex;
            if (e.getBindingResult().getFieldError() != null) {
                msg = e.getBindingResult().getFieldError().getDefaultMessage();
            }
        }
        return ApiResult.fail(PlatformErrorCode.BAD_REQUEST, msg);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<Void> onBadBody(HttpMessageNotReadableException ex) {
        return ApiResult.fail(PlatformErrorCode.BAD_REQUEST, "请求体无法解析");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<Void> onIllegalArgument(IllegalArgumentException ex) {
        return ApiResult.fail(PlatformErrorCode.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<Void> onIllegalState(IllegalStateException ex) {
        return ApiResult.fail(AiotErrorCode.QUERY_EXECUTION_FAILED, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<Void> onAny(Exception ex) {
        return ApiResult.fail(PlatformErrorCode.INTERNAL_ERROR,
                ex.getMessage() != null ? ex.getMessage() : PlatformErrorCode.INTERNAL_ERROR.getMessage());
    }
}
