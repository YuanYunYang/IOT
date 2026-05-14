package com.iot.platform.aiot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.platform.aiot.config.AiotProperties;
import com.iot.platform.aiot.llm.OpenAiChatDtos;
import com.iot.platform.aiot.llm.OpenAiCompatibleChatClient;
import com.iot.platform.aiot.nlp.DataSchemaPromptBuilder;
import com.iot.platform.aiot.query.QueryExecutor;
import com.iot.platform.aiot.query.SqlPlan;
import com.iot.platform.aiot.query.SqlSafetyValidator;
import com.iot.platform.aiot.report.ExcelReportService;
import com.iot.platform.aiot.report.ReportCacheService;
import com.iot.platform.aiot.web.dto.NlDataChatResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * “真AI”链路：
 * 中文问题 -> LLM 产出 JSON(数据源 + SQL) -> 安全校验 -> 自动执行 -> LLM 中文总结 -> 返回前端。
 */
@Service
public class AiChatService {

    private final AiotProperties properties;
    private final OpenAiCompatibleChatClient chatClient;
    private final DataSchemaPromptBuilder schemaPromptBuilder;
    private final QueryExecutor queryExecutor;
    private final ObjectMapper objectMapper;
    private final ReportCacheService reportCacheService;
    private final SqlSafetyValidator sqlSafetyValidator = new SqlSafetyValidator();

    public AiChatService(AiotProperties properties,
                         OpenAiCompatibleChatClient chatClient,
                         DataSchemaPromptBuilder schemaPromptBuilder,
                         QueryExecutor queryExecutor,
                         ObjectMapper objectMapper,
                         ReportCacheService reportCacheService) {
        this.properties = properties;
        this.chatClient = chatClient;
        this.schemaPromptBuilder = schemaPromptBuilder;
        this.queryExecutor = queryExecutor;
        this.objectMapper = objectMapper;
        this.reportCacheService = reportCacheService;
    }

    /**
     * 完整对话查数管线。仅在配置项 autoExecuteQuery（AiotProperties）为 true 时执行 SQL；
     * 为 false 时仍会做规划与校验，但结果集 rows 为空，总结阶段仅依据规划说明答复。
     */
    public NlDataChatResponse chat(AiotProperties.Provider provider, String modelOverride, String userMessage) {
        AiotProperties.Provider p = provider != null ? provider : properties.getDefaultProvider();

        // 1) 调 LLM 产出 SqlPlan（JSON：数据源 + SQL + 说明）
        SqlPlan plan = planSql(p, modelOverride, userMessage);

        // 2) 服务端只读安全校验（防多语句、无 LIMIT 等）
        sqlSafetyValidator.validateSelectOnly(plan.getSql());

        // 3) 按数据源执行（关闭 autoExecuteQuery 时跳过，rows 保持空列表）
        List<Map<String, Object>> rows = new ArrayList<>();
        String datasource = safeLower(plan.getDatasource());
        if (properties.isAutoExecuteQuery()) {
            if ("mysql".equals(datasource)) {
                rows = queryExecutor.executeMysql(plan.getSql());
            } else if ("clickhouse".equals(datasource) || "ck".equals(datasource)) {
                rows = queryExecutor.executeClickHouse(plan.getSql());
                datasource = "clickhouse";
            } else {
                throw new IllegalArgumentException("未知数据源：" + plan.getDatasource());
            }
        }

        // 限制返回行数
        int maxRows = Math.max(1, properties.getMaxRows());
        if (rows.size() > maxRows) {
            rows = rows.subList(0, maxRows);
        }

        // 4) 将问题 + SQL + 结果行再喂给 LLM，生成中文结论（禁止模型再输出 SQL）
        String reply = summarize(p, modelOverride, userMessage, datasource, plan.getSql(), rows, plan.getExplanation());

        // 组装响应对象
        NlDataChatResponse r = new NlDataChatResponse();
        r.setProvider(p.name());
        r.setModel(plan.getModel());
        r.setIntent("LLM_SQL");
        r.setDataSources(java.util.Collections.singletonList(datasource));
        r.setExecuted(true);
        r.setExecutedSql(plan.getSql());
        r.setRows(rows);
        r.setReply(reply);

        // 有结果时写入 Redis，短时内可用 reportId 下载 Excel，避免重复查询
        if (rows != null && !rows.isEmpty()) {
            String filename = ExcelReportService.safeFilename("aiot-result", "aiot-report");
            String reportId = reportCacheService.put(filename, rows, plan.getSql());
            r.setReportId(reportId);
            r.setReportFilename(filename);
        }

        r.setHints(java.util.Arrays.asList(
                "该 SQL 由大模型生成并经服务端只读校验后执行。",
                "如需更精确字段/口径，请在问题中补充设备类型、点位编码、时间范围等。"
        ));
        return r;
    }

    /** 第一轮 LLM：system（规划约束）+ system（数据字典）+ user（中文问题）→ 解析为 SqlPlan */
    private SqlPlan planSql(AiotProperties.Provider provider, String modelOverride, String userMessage) {
        String system = buildPlannerSystemPrompt();
        String schema = schemaPromptBuilder.buildFullSchemaPrompt();
        String user = "用户问题（中文）：\n" + userMessage;

        OpenAiChatDtos.ChatCompletionRequest req = new OpenAiChatDtos.ChatCompletionRequest();
        req.setTemperature(0.1);
        req.setMessages(java.util.Arrays.asList(
                OpenAiChatDtos.Message.system(system),
                OpenAiChatDtos.Message.system(schema),
                OpenAiChatDtos.Message.user(user)
        ));
        // 尽力启用 JSON 输出模式（依厂商支持情况）
        Map<String, Object> responseFormat = new LinkedHashMap<>();
        responseFormat.put("type", "json_object");
        req.setResponse_format(responseFormat);

        String content = chatClient.chat(provider, modelOverride, req);
        SqlPlan plan = chatClient.readJson(stripCodeFences(content), SqlPlan.class);
        if (plan.getSql() == null || plan.getSql().trim().isEmpty()) {
            throw new IllegalArgumentException("大模型未返回有效 SQL。原始内容=" + content);
        }
        if (plan.getDatasource() == null || plan.getDatasource().trim().isEmpty()) {
            // 默认分析库使用 clickhouse
            plan.setDatasource("clickhouse");
        }
        plan.setModel(resolveModel(provider, modelOverride));
        return plan;
    }

    private String resolveModel(AiotProperties.Provider provider, String modelOverride) {
        if (modelOverride != null && !modelOverride.trim().isEmpty()) {
            return modelOverride.trim();
        }
        if (provider == AiotProperties.Provider.DEEPSEEK) {
            return properties.getProviders().getDeepseek().getModel();
        }
        return properties.getProviders().getQwen().getModel();
    }

    /** 第二轮 LLM：基于查询结果做中文解读，输入序列化为 JSON，避免模型编造未返回的数据 */
    private String summarize(AiotProperties.Provider provider,
                             String modelOverride,
                             String userMessage,
                             String datasource,
                             String sql,
                             List<Map<String, Object>> rows,
                             String plannerExplanation) {
        String system = "你是 IoT 平台的数据分析助手。你将基于查询结果用中文给出结论。"
                + "要求：先给结论，再给关键数据要点；若无数据，说明可能原因与下一步建议。"
                + "禁止输出任何 SQL。";
        String payload;
        try {
            Map<String, Object> obj = new LinkedHashMap<>();
            obj.put("question", userMessage);
            obj.put("datasource", datasource);
            obj.put("sql", sql);
            obj.put("planner_explain", plannerExplanation);
            obj.put("rows", rows);
            payload = objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            payload = "{\"question\":\"" + safeJson(userMessage) + "\",\"rows\":" + rows.size() + "}";
        }

        OpenAiChatDtos.ChatCompletionRequest req = new OpenAiChatDtos.ChatCompletionRequest();
        req.setTemperature(0.2);
        req.setMessages(java.util.Arrays.asList(
                OpenAiChatDtos.Message.system(system),
                OpenAiChatDtos.Message.user("请根据以下 JSON 数据回答：\n" + payload)
        ));
        return chatClient.chat(provider, modelOverride, req).trim();
    }

    private static String buildPlannerSystemPrompt() {
        return "你是 IoT 平台的 NL2SQL 规划器。你必须输出严格 JSON（不要 markdown，不要代码块）。\n"
                + "JSON schema：\n"
                + "{\n"
                + "  \"datasource\": \"clickhouse\" | \"mysql\",\n"
                + "  \"sql\": \"SELECT ... LIMIT N\",\n"
                + "  \"explanation\": \"中文解释（1-2 句）\"\n"
                + "}\n"
                + "硬性约束：\n"
                + "- 只能生成 SELECT（可用 WITH），禁止任何写操作。\n"
                + "- 必须带 LIMIT，且 N <= 500。\n"
                + "- 只能使用提供的数据字典中的表/列；不要臆造。\n"
                + "- 报警历史/点位历史等分析默认优先 ClickHouse。\n";
    }

    private static String stripCodeFences(String s) {
        if (s == null) {
            return "";
        }
        String t = s.trim();
        if (t.startsWith("```")) {
            // 去掉首尾 Markdown 代码围栏行
            int firstNewline = t.indexOf('\n');
            if (firstNewline > 0) {
                t = t.substring(firstNewline + 1);
            }
            if (t.endsWith("```")) {
                t = t.substring(0, t.length() - 3);
            }
        }
        return t.trim();
    }

    private static String safeLower(String s) {
        return s == null ? "" : s.trim().toLowerCase(java.util.Locale.ROOT);
    }

    private static String safeJson(String s) {
        if (s == null) return "";
        return s.replace("\\\\", "\\\\\\\\").replace("\"", "\\\\\"");
    }
}
