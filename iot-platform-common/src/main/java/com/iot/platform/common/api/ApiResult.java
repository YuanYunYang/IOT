package com.iot.platform.common.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * 统一 REST 响应包装。成功时 success 为 true 且 code 为 0。
 */
@Schema(description = "统一 REST 响应：成功时 success=true 且 code=0，data 为业务数据。")
public class ApiResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 是否成功（与 code==0 一致，便于前端判断）。 */
    @Schema(description = "是否成功，与 code 是否表示成功一致。")
    private boolean success;

    /** 业务错误码，成功时与 PlatformErrorCode.SUCCESS 一致 */
    @Schema(description = "业务状态码，0 表示成功；非 0 为具体错误码。")
    private int code;

    /** 提示文案；失败时为错误说明 */
    @Schema(description = "提示信息；失败时为错误说明。")
    private String message;

    /** 载荷；失败时可为 null */
    @Schema(description = "业务数据载荷；失败时可为 null。")
    private T data;

    /** 服务端生成时间戳（毫秒）。 */
    @Schema(description = "服务端响应时间戳（毫秒）。")
    private long timestamp;

    public static <T> ApiResult<T> ok(T data) {
        ApiResult<T> r = new ApiResult<>();
        r.success = true;
        r.code = PlatformErrorCode.SUCCESS.getCode();
        r.message = PlatformErrorCode.SUCCESS.getMessage();
        r.data = data;
        r.timestamp = System.currentTimeMillis();
        return r;
    }

    public static <T> ApiResult<T> fail(ErrorCode errorCode) {
        return fail(errorCode, null);
    }

    public static <T> ApiResult<T> fail(ErrorCode errorCode, String overrideMessage) {
        ApiResult<T> r = new ApiResult<>();
        r.success = false;
        r.code = errorCode.getCode();
        r.message = overrideMessage != null && !overrideMessage.isEmpty() ? overrideMessage : errorCode.getMessage();
        r.timestamp = System.currentTimeMillis();
        return r;
    }

    /**
     * 兼容旧代码：直接使用数值码（建议逐步迁移为 ErrorCode 接口）。
     */
    public static <T> ApiResult<T> fail(int code, String message) {
        ApiResult<T> r = new ApiResult<>();
        r.success = false;
        r.code = code;
        r.message = message;
        r.timestamp = System.currentTimeMillis();
        return r;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
