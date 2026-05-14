package com.iot.platform.aiot.nlp;

import com.iot.platform.aiot.schema.PlatformClickHouseCatalog;
import com.iot.platform.aiot.schema.PlatformMysqlCatalog;
import org.springframework.stereotype.Component;

/**
 * 组装注入大模型的「数据字典 + 约束」文本。后续接入 LLM 时整段作为 system 或 tool 上下文。
 */
@Component
public class DataSchemaPromptBuilder {

    public String buildFullSchemaPrompt() {
        return "你是 IoT 平台的数据分析助手。用户用中文描述查询或统计需求。\n"
                + "你只能使用下方给出的表与列；不要臆造表名或列名。\n"
                + "若需要时间范围，默认使用 Asia/Shanghai；ClickHouse 中注意 DateTime 与时区。\n"
                + "对报警历史的大范围统计优先使用 ClickHouse；单条明细或强一致联查可用 MySQL。\n"
                + "生成 SQL 时仅允许 SELECT，禁止 INSERT/UPDATE/DELETE/DROP；必要时加 LIMIT。\n"
                + "\n"
                + "【MySQL 表说明】\n"
                + PlatformMysqlCatalog.ALL_FOR_LLM.trim()
                + "\n\n"
                + "【ClickHouse 表说明】\n"
                + PlatformClickHouseCatalog.ALL_FOR_LLM.trim()
                + "\n";
    }
}
