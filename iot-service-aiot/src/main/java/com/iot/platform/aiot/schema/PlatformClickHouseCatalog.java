package com.iot.platform.aiot.schema;

/**
 * ClickHouse 侧规划表（写入管道落地后与此对齐）。表名采用项目约定驼峰形式，SQL 中需用反引号包裹。
 * <p>
 * 与 MySQL 表 alarm_record 相比，分析库中的表 alarmRecord 建议冗余 device_type_id / device_type_name，
 * 便于「按设备类型、时间」排序与聚合而无需再 JOIN MySQL。
 */
public final class PlatformClickHouseCatalog {

    private PlatformClickHouseCatalog() {
    }

    public static final String TABLE_ALARM_RECORD = "alarmRecord";

    public static final String TABLE_DEVICE_POINT_HISTORY = "devicePointHistory";

    public static final String TABLE_DEVICE_POINT_HISTORY_HOUR = "devicePointHistoryHour";

    public static final String ALARM_RECORD =
            "表 alarmRecord（报警历史，分析用）\n"
                    + "建议列（与业务写入约定一致即可）：\n"
                    + "- id: UInt64 或 String（与业务主键对齐策略）\n"
                    + "- rule_id: UInt64\n"
                    + "- device_id: UInt64\n"
                    + "- device_type_id: UInt64（冗余，便于按设备类型筛选/排序）\n"
                    + "- device_type_name: String（冗余展示名）\n"
                    + "- title, message: String\n"
                    + "- alarm_level: LowCardinality(String)\n"
                    + "- point_value_snapshot: String\n"
                    + "- triggered_at: DateTime64(3)（建议存 Asia/Shanghai 或统一 UTC 并在查询中转换）\n"
                    + "- acknowledged: UInt8\n"
                    + "主键/分区：可按 (toYYYYMMDD(triggered_at), device_type_id) 分区，ORDER BY (triggered_at, device_id)。\n";

    public static final String DEVICE_POINT_HISTORY =
            "表 devicePointHistory（点位明细历史）\n"
                    + "建议列：\n"
                    + "- device_id: UInt64\n"
                    + "- device_type_id: UInt64（可选）\n"
                    + "- point_code: LowCardinality(String)\n"
                    + "- ts: DateTime64(3) 采集时间\n"
                    + "- value: Float64 或 String（与 value_type 一致）\n"
                    + "- quality: Nullable(UInt8) 可选\n"
                    + "典型查询：某设备某点位时间范围曲线、聚合。\n";

    public static final String DEVICE_POINT_HISTORY_HOUR =
            "表 devicePointHistoryHour（点位按整点汇总）\n"
                    + "建议列：\n"
                    + "- device_id: UInt64\n"
                    + "- point_code: LowCardinality(String)\n"
                    + "- hour_ts: DateTime（整点，如 toStartOfHour）\n"
                    + "- avg_value, max_value, min_value, sum_value: 视业务而定\n"
                    + "典型查询：按小时能耗、趋势对比。\n";

    /**
     * 建表示例（MergeTree 仅作参考，分区/排序按数据量调整）。
     */
    public static final String DDL_ALARM_RECORD_EXAMPLE =
            "CREATE TABLE IF NOT EXISTS alarmRecord (\n"
                    + "  id UInt64,\n"
                    + "  rule_id UInt64,\n"
                    + "  device_id UInt64,\n"
                    + "  device_type_id UInt64,\n"
                    + "  device_type_name String,\n"
                    + "  title String,\n"
                    + "  message String,\n"
                    + "  alarm_level LowCardinality(String),\n"
                    + "  point_value_snapshot String,\n"
                    + "  triggered_at DateTime64(3),\n"
                    + "  acknowledged UInt8\n"
                    + ") ENGINE = MergeTree()\n"
                    + "PARTITION BY toYYYYMMDD(triggered_at)\n"
                    + "ORDER BY (device_type_id, triggered_at, device_id);\n";

    public static final String EXAMPLE_ALARM_LAST_7_DAYS_BY_TYPE =
            "SELECT\n"
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
                    + "FROM alarmRecord\n"
                    + "WHERE triggered_at >= now() - INTERVAL 7 DAY\n"
                    + "ORDER BY device_type_name DESC, triggered_at DESC\n"
                    + "LIMIT 500;\n";

    public static final String ALL_FOR_LLM = String.join("\n\n",
            ALARM_RECORD,
            DEVICE_POINT_HISTORY,
            DEVICE_POINT_HISTORY_HOUR
    );
}
