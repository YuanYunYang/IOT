package com.iot.platform.starter.clickhouse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

/**
 * ClickHouse HTTP 驱动的轻量 JDBC 工具，非完整 ORM。
 */
public class ClickHouseHelper {

    private final IotClickHouseProperties properties;

    public ClickHouseHelper(IotClickHouseProperties properties) {
        this.properties = properties;
    }

    public Connection getConnection() throws SQLException {
        Properties p = new Properties();
        p.setProperty("user", properties.getUsername() != null ? properties.getUsername() : "");
        p.setProperty("password", properties.getPassword() != null ? properties.getPassword() : "");
        p.setProperty("socket_timeout", String.valueOf(properties.getSocketTimeoutMs()));
        return DriverManager.getConnection(properties.getUrl(), p);
    }

    public int executeUpdate(String sql, Object... params) throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            return ps.executeUpdate();
        }
    }

    public void execute(String sql) throws SQLException {
        try (Connection c = getConnection();
             Statement st = c.createStatement()) {
            st.execute(sql);
        }
    }

    public <T> List<T> queryList(String sql, Function<ResultSet, T> rowMapper, Object... params) throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(rowMapper.apply(rs));
                }
                return out;
            }
        }
    }

    public List<Map<String, Object>> queryMaps(String sql, Object... params) throws SQLException {
        return queryList(sql, rs -> {
            try {
                ResultSetMetaData md = rs.getMetaData();
                int cols = md.getColumnCount();
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= cols; i++) {
                    row.put(md.getColumnLabel(i), rs.getObject(i));
                }
                return row;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, params);
    }

    public Map<String, Object> queryOneMap(String sql, Object... params) throws SQLException {
        List<Map<String, Object>> list = queryMaps(sql, params);
        return list.isEmpty() ? null : list.get(0);
    }

    public int batchInsert(String insertSql, List<Object[]> rows) throws SQLException {
        if (rows.isEmpty()) {
            return 0;
        }
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(insertSql)) {
            for (Object[] row : rows) {
                bind(ps, row);
                ps.addBatch();
            }
            int[] r = ps.executeBatch();
            int sum = 0;
            for (int x : r) {
                sum += x;
            }
            return sum;
        }
    }

    private static void bind(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }
}
