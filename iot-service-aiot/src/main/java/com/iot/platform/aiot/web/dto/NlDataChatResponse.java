package com.iot.platform.aiot.web.dto;

import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 中文对话式数据查询的返回：自然语言回复 + 结构化意图与示例 SQL，便于前端展示与后续接 LLM。
 */
@Data
public class NlDataChatResponse {

    /** 给用户看的说明（接入 LLM 后可改为模型总结 + 表格解读）。 */
    private String reply;

    /** 本次使用的 LLM provider，如 QWEN / DEEPSEEK。 */
    private String provider;

    /** 本次使用的模型 id（若请求指定）。 */
    private String model;

    /** 粗粒度意图编码，如 ALARM_HISTORY_CLICKHOUSE。 */
    private String intent;

    /** 涉及的数据源，如 clickhouse:alarmRecord。 */
    private List<String> dataSources = Collections.emptyList();

    /**
     * 建议的 ClickHouse 查询（只读）；执行前必须经过安全校验，当前默认不自动执行。
     */
    private String suggestedClickHouseSql;

    /** 若将来需要查 MySQL，可填建议 SQL。 */
    private String suggestedMysqlSql;

    /** 是否已在服务端自动执行查询。 */
    private boolean executed;

    /** 实际执行的只读 SQL（服务端校验通过后）。 */
    private String executedSql;

    /** 查询结果（行列表，列名为 key）；行数会被截断。 */
    private List<Map<String, Object>> rows = Collections.emptyList();

    /**
     * 若返回中包含表格数据，可提供 reportId 供用户确认后下载 Excel。
     * reportId 在服务端 Redis 中有 TTL（临时有效）。
     */
    private String reportId;

    /** 建议下载文件名（不含路径）。 */
    private String reportFilename;

    /**
     * 注入 LLM 用的完整数据字典片段预览（过长会截断）；正式环境可对运营/调试接口单独开放全文。
     */
    private String schemaPromptPreview;

    private List<String> hints = Collections.emptyList();

    public static NlDataChatResponse empty(String reply) {
        NlDataChatResponse r = new NlDataChatResponse();
        r.setReply(reply);
        r.setIntent("EMPTY");
        return r;
    }
}
