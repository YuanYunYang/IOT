package com.iot.platform.aiot.query;

import com.iot.platform.starter.clickhouse.ClickHouseHelper;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 执行 NL2SQL 产出的只读查询：ClickHouse 走平台封装；MySQL 使用 Spring 默认数据源对应的 JdbcTemplate。
 */
@Component
public class QueryExecutor {

    private final ClickHouseHelper clickHouseHelper;
    /** 未配置 MySQL 数据源时为空，调用 executeMysql 会失败 */
    private final JdbcTemplate jdbcTemplate;

    public QueryExecutor(ClickHouseHelper clickHouseHelper, org.springframework.beans.factory.ObjectProvider<JdbcTemplate> jdbcTemplateProvider) {
        this.clickHouseHelper = clickHouseHelper;
        this.jdbcTemplate = jdbcTemplateProvider.getIfAvailable();
    }

    /** 执行已通过 SqlSafetyValidator 校验的 ClickHouse SQL */
    public List<Map<String, Object>> executeClickHouse(String sql) {
        try {
            return clickHouseHelper.queryMaps(sql);
        } catch (SQLException e) {
            throw new IllegalStateException("ClickHouse 查询失败：" + e.getMessage(), e);
        }
    }

    /** 执行已通过校验的 MySQL 只读 SQL（需存在 JdbcTemplate bean） */
    public List<Map<String, Object>> executeMysql(String sql) {
        if (jdbcTemplate == null) {
            throw new IllegalStateException("未配置 MySQL 数据源（缺少 JdbcTemplate Bean）");
        }
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            return rows != null ? rows : Collections.emptyList();
        } catch (DataAccessException e) {
            throw new IllegalStateException("MySQL 查询失败：" + e.getMessage(), e);
        }
    }
}

