package com.iot.platform.aiot.report;

import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 对话查询结果在 Redis 中的缓存结构，供短时内生成 Excel 下载，避免重复跑 SQL。
 */
@Data
public class CachedReport {

    /** ISO-8601 创建时间 */
    private String createdAt;

    /** 建议下载文件名 */
    private String filename;

    /** 生成结果集时执行的只读 SQL（审计/排错） */
    private String executedSql;

    /** 结果行，列名即 Map 的 key */
    private List<Map<String, Object>> rows = Collections.emptyList();
}

