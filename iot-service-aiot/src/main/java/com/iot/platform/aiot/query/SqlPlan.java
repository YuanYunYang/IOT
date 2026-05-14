package com.iot.platform.aiot.query;

import lombok.Data;

/**
 * 大模型应输出的规划 JSON（或兼容子集）；字段刻意精简以便校验与安全执行。
 */
@Data
public class SqlPlan {

    /**
     * 数据源：clickhouse 或 mysql
     */
    private String datasource;

    /**
     * 仅允许 SELECT，且必须包含 LIMIT。
     */
    private String sql;

    /**
     * 给用户看的简短中文说明。
     */
    private String explanation;

    /**
     * 可选：本次使用的模型 ID。
     */
    private String model;
}

