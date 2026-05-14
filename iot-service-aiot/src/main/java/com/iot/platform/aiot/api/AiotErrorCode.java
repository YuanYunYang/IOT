package com.iot.platform.aiot.api;

import com.iot.platform.common.api.ErrorCode;

/**
 * AIOT 服务业务错误码（建议占用 12000–12999，便于与平台码区分）。
 * 后续可在本枚举中持续追加，或新增独立枚举实现 ErrorCode 接口。
 */
public enum AiotErrorCode implements ErrorCode {

    LLM_CALL_FAILED(12001, "大模型调用失败"),
    SQL_PLAN_INVALID(12002, "无法生成有效的查询计划"),
    SQL_REJECTED(12003, "SQL 未通过安全校验"),
    QUERY_EXECUTION_FAILED(12004, "数据查询执行失败"),
    REPORT_NOT_FOUND(12005, "报表已过期或不存在");

    private final int code;
    private final String message;

    AiotErrorCode(int code, String message) {
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
