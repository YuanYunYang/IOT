package com.iot.platform.common.api;

/**
 * 平台级通用错误码（1000–1999）。各业务服务请使用独立号段扩展。
 */
public enum PlatformErrorCode implements ErrorCode {

    SUCCESS(0, "OK"),

    UNAUTHORIZED(1001, "未授权或访问令牌无效"),
    FORBIDDEN(1003, "无访问权限"),

    BAD_REQUEST(1400, "请求参数错误"),
    NOT_FOUND(1404, "资源不存在"),

    INTERNAL_ERROR(1500, "服务器内部错误"),
    UPSTREAM_ERROR(1502, "调用下游服务失败");

    private final int code;
    private final String message;

    PlatformErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
