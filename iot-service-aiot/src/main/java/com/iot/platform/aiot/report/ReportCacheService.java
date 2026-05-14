package com.iot.platform.aiot.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 将报表结果暂存 Redis（短 TTL），用户确认后再下载。
 * 避免下载时再次调用大模型或重复执行重查询。
 */
@Service
public class ReportCacheService {

    private static final String KEY_PREFIX = "aiot:report:";
    private static final Duration TTL = Duration.ofMinutes(10);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public ReportCacheService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public String put(String filename, List<Map<String, Object>> rows, String executedSql) {
        String id = UUID.randomUUID().toString().replace("-", "");
        String key = KEY_PREFIX + id;
        CachedReport payload = new CachedReport();
        payload.setCreatedAt(Instant.now().toString());
        payload.setFilename(filename);
        payload.setRows(rows);
        payload.setExecutedSql(executedSql);
        try {
            String json = objectMapper.writeValueAsString(payload);
            redisTemplate.opsForValue().set(key, json, TTL);
            return id;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to cache report: " + e.getMessage(), e);
        }
    }

    public CachedReport get(String reportId) {
        if (reportId == null || reportId.trim().isEmpty()) {
            return null;
        }
        String key = KEY_PREFIX + reportId.trim();
        String json = redisTemplate.opsForValue().get(key);
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, CachedReport.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read cached report: " + e.getMessage(), e);
        }
    }
}

