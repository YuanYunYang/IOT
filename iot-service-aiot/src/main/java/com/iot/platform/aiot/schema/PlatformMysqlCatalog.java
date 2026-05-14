package com.iot.platform.aiot.schema;

/**
 * 与现有各服务 JPA 实体表一致，供 NL→SQL / 报表生成时作为 MySQL 侧「数据字典」注入 LLM。
 * <p>
 * 表名与列名以实体为准；此处为中文说明，便于用户用自然语言描述「查哪张表、按什么条件」。
 */
public final class PlatformMysqlCatalog {

    private PlatformMysqlCatalog() {
    }

    /** 对应实体 AlarmRecord，表名 alarm_record */
    public static final String ALARM_RECORD =
            "表 alarm_record（报警触发记录）\n"
                    + "- id: 主键\n"
                    + "- rule_id: 关联规则 alarm_rule.id\n"
                    + "- device_id: 设备 ID\n"
                    + "- title, message: 报警标题与详情\n"
                    + "- alarm_level: 级别\n"
                    + "- point_value_snapshot: 触发时点位值快照\n"
                    + "- triggered_at: 触发时间（UTC 存储，查询时注意时区）\n"
                    + "- acknowledged: 是否已确认\n"
                    + "说明：实时业务库；历史分析大量数据建议走 ClickHouse alarmRecord。\n";

    /** 对应实体 AlarmRule，表名 alarm_rule */
    public static final String ALARM_RULE =
            "表 alarm_rule（报警规则）\n"
                    + "- id, name, enabled, subsystem\n"
                    + "- device_type_id, device_type_name, device_id, device_name\n"
                    + "- point_code, point_id, point_name\n"
                    + "- compare_operator, threshold_value, duration_seconds\n"
                    + "- telemetry_redis_key: Redis 实时值 key\n"
                    + "- alarm_title, alarm_level\n"
                    + "- created_at, updated_at\n";

    /** 对应实体 IotDevice，表名 device */
    public static final String DEVICE =
            "表 device（设备）\n"
                    + "- id, subsystem, device_type_id, device_name, location\n"
                    + "- external_device_id, remark, created_at, updated_at\n";

    /** 对应实体 IotDeviceType，表名 device_type */
    public static final String DEVICE_TYPE =
            "表 device_type（设备类型）\n"
                    + "- id, type_code, type_name, subsystem, remark, created_at, updated_at\n";

    /** 对应实体 IotDevicePoint，表名 device_point */
    public static final String DEVICE_POINT =
            "表 device_point（某设备类型下的点位定义）\n"
                    + "- id, device_type_id, point_code, point_name, unit, value_type\n"
                    + "- external_point_id, enabled, sort_order, remark, created_at, updated_at\n";

    /** 对应实体 SysUser，表名 sys_user */
    public static final String SYS_USER =
            "表 sys_user（用户）\n"
                    + "- id, username, password_hash, real_name, enabled, created_at, updated_at\n"
                    + "注意：密码列禁止在查询结果中返回给前端或写入 LLM 上下文。\n";

    public static final String SYS_ROLE =
            "表 sys_role（角色）\n"
                    + "- id, role_code, role_name, remark, created_at\n";

    public static final String SYS_PERMISSION =
            "表 sys_permission（权限）\n"
                    + "- id, parent_id, perm_type, perm_code, name, path, http_method, sort_order, created_at\n";

    public static final String ALL_FOR_LLM = String.join("\n\n",
            ALARM_RECORD,
            ALARM_RULE,
            DEVICE,
            DEVICE_TYPE,
            DEVICE_POINT,
            SYS_USER,
            SYS_ROLE,
            SYS_PERMISSION
    );
}
