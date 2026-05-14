package com.iot.platform.aiot.nlp;

import com.iot.platform.aiot.schema.PlatformClickHouseCatalog;
import com.iot.platform.aiot.web.dto.NlDataChatResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 中文自然语言 → 意图与示例 SQL（当前为规则启发式；接入 LLM 后可用 DataSchemaPromptBuilder 生成提示词再解析模型输出）。
 */
@Service
public class NaturalLanguageDataQueryService {

    private static final Pattern DAYS = Pattern.compile("(近|最近)\\s*(\\d+)\\s*天");

    private final DataSchemaPromptBuilder schemaPromptBuilder;

    public NaturalLanguageDataQueryService(DataSchemaPromptBuilder schemaPromptBuilder) {
        this.schemaPromptBuilder = schemaPromptBuilder;
    }

    /**
     * 不调用 LLM：用关键词识别「报警历史 / 点位明细 / 小时聚合」等意图，返回示例 SQL 与 schema 预览。
     * 与 AiChatService 的全链路 NL2SQL 可并存（不同接口）。
     */
    public NlDataChatResponse plan(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return NlDataChatResponse.empty("请输入中文描述，例如：近7天报警历史按设备类型与时间倒序。");
        }
        String text = userMessage.trim();
        String lower = text.toLowerCase(Locale.ROOT);

        NlDataChatResponse r = new NlDataChatResponse();
        r.setSchemaPromptPreview(truncate(schemaPromptBuilder.buildFullSchemaPrompt(), 4000));

        if (mentionsAlarmHistory(text, lower)) {
            int days = extractDays(text, 7);
            r.setIntent("ALARM_HISTORY_CLICKHOUSE");
            r.setDataSources(Collections.singletonList("clickhouse:" + PlatformClickHouseCatalog.TABLE_ALARM_RECORD));
            r.setSuggestedClickHouseSql(buildAlarmHistorySql(days));
            r.setReply(String.format(
                    "已理解：查询近 %d 天报警历史，按设备类型名称、触发时间倒序。"
                            + " 请在 ClickHouse 表 `%s` 上执行建议 SQL（需已冗余 device_type_name 等列）。"
                            + " 正式接入 LLM 后，将由模型在相同数据字典约束下生成并校验 SQL。",
                    days,
                    PlatformClickHouseCatalog.TABLE_ALARM_RECORD
            ));
            r.setHints(Arrays.asList(
                    "生产环境执行 SQL 前须做只读校验、行数上限与白名单表检查。",
                    "若 CK 尚未同步 device_type_name，需先在写入链路中从 device/device_type 冗余。"
            ));
            return r;
        }

        if (mentionsPointHistory(text, lower) && !lower.contains("整点") && !lower.contains("小时")) {
            r.setIntent("DEVICE_POINT_HISTORY_CLICKHOUSE");
            r.setDataSources(Collections.singletonList("clickhouse:" + PlatformClickHouseCatalog.TABLE_DEVICE_POINT_HISTORY));
            r.setSuggestedClickHouseSql(
                    "SELECT device_id, point_code, ts, value\n"
                            + "FROM devicePointHistory\n"
                            + "WHERE ts >= now() - INTERVAL 7 DAY\n"
                            + "ORDER BY ts DESC\n"
                            + "LIMIT 1000;\n"
            );
            r.setReply("已识别为点位明细历史查询（示例为近7天）。请按实际时间范围与设备/点位条件改写 WHERE。");
            r.setHints(Collections.singletonList("明细量大，务必 LIMIT；聚合趋势可改用 devicePointHistoryHour。"));
            return r;
        }

        if (mentionsPointHistory(text, lower) && (lower.contains("整点") || lower.contains("小时") || lower.contains("按小时"))) {
            r.setIntent("DEVICE_POINT_HISTORY_HOUR_CLICKHOUSE");
            r.setDataSources(Collections.singletonList("clickhouse:" + PlatformClickHouseCatalog.TABLE_DEVICE_POINT_HISTORY_HOUR));
            r.setSuggestedClickHouseSql(
                    "SELECT device_id, point_code, hour_ts, avg_value, max_value, min_value\n"
                            + "FROM devicePointHistoryHour\n"
                            + "WHERE hour_ts >= now() - INTERVAL 7 DAY\n"
                            + "ORDER BY hour_ts DESC\n"
                            + "LIMIT 500;\n"
            );
            r.setReply("已识别为按整点/小时汇总的点位历史查询（示例为近7天）。");
            return r;
        }

        r.setIntent("UNKNOWN");
        r.setReply("暂未匹配到明确意图。接入大模型后，将结合数据字典解析你的描述；"
                + "当前可参考 schemaPromptPreview 中的表结构自行编写 SQL。");
        r.setHints(new ArrayList<String>());
        return r;
    }

    private static boolean mentionsAlarmHistory(String original, String lower) {
        boolean alarm = original.contains("报警") || lower.contains("alarm");
        boolean history = original.contains("历史") || original.contains("记录");
        return alarm && history;
    }

    private static boolean mentionsPointHistory(String original, String lower) {
        return (original.contains("点位") && (original.contains("历史") || original.contains("曲线")))
                || lower.contains("telemetry")
                || original.contains("时序");
    }

    private static int extractDays(String text, int defaultDays) {
        Matcher m = DAYS.matcher(text);
        if (m.find()) {
            try {
                return Math.min(Math.max(1, Integer.parseInt(m.group(2))), 365);
            } catch (NumberFormatException ignored) {
                return defaultDays;
            }
        }
        if (text.contains("7") && (text.contains("天") || text.contains("日"))) {
            return 7;
        }
        return defaultDays;
    }

    private static String buildAlarmHistorySql(int days) {
        return "SELECT\n"
                + "  id,\n"
                + "  rule_id,\n"
                + "  device_id,\n"
                + "  device_type_id,\n"
                + "  device_type_name,\n"
                + "  title,\n"
                + "  message,\n"
                + "  alarm_level,\n"
                + "  triggered_at,\n"
                + "  acknowledged\n"
                + "FROM `" + PlatformClickHouseCatalog.TABLE_ALARM_RECORD + "`\n"
                + "WHERE triggered_at >= now() - INTERVAL " + days + " DAY\n"
                + "ORDER BY device_type_name DESC, triggered_at DESC\n"
                + "LIMIT 500;\n";
    }

    private static String truncate(String s, int max) {
        if (s.length() <= max) {
            return s;
        }
        return s.substring(0, max) + "\n…(truncated)";
    }
}
