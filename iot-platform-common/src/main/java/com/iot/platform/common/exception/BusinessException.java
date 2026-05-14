package com.iot.platform.common.exception;

import com.iot.platform.common.api.ErrorCode;
import com.iot.platform.common.api.PlatformErrorCode;

/**
 * 携带 ErrorCode 的业务异常，供全局异常处理器统一转换为 ApiResult。
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final ErrorCode errorCode;
    private final String overrideMessage;

    public BusinessException(ErrorCode errorCode) {
        this(errorCode, null, null);
    }

    public BusinessException(ErrorCode errorCode, String overrideMessage) {
        this(errorCode, overrideMessage, null);
    }

    public BusinessException(ErrorCode errorCode, String overrideMessage, Throwable cause) {
        super(overrideMessage != null ? overrideMessage : errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.overrideMessage = overrideMessage;
    }

    public static BusinessException of(ErrorCode code) {
        return new BusinessException(code);
    }

    public static BusinessException of(ErrorCode code, String message) {
        return new BusinessException(code, message);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /** 返回对外 message：优先覆盖文案，否则用错误码默认文案。 */
    public String getDisplayMessage() {
        return overrideMessage != null && !overrideMessage.isEmpty() ? overrideMessage : errorCode.getMessage();
    }

    /** 未知异常包装为 PlatformErrorCode.INTERNAL_ERROR。 */
    public static BusinessException wrap(Throwable cause) {
        return new BusinessException(PlatformErrorCode.INTERNAL_ERROR, cause != null ? cause.getMessage() : null, cause);
    }
}
