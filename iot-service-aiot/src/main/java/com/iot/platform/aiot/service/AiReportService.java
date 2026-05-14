package com.iot.platform.aiot.service;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * AI 报表占位：后续可接入 ClickHouse 聚合 + LLM 摘要。
 */
@Service
public class AiReportService {

    public Map<String, Object> buildSampleReport() {
        return Collections.singletonMap(
                "summary",
                "占位：此处接入 ClickHouse/MySQL 查询与 LLM 摘要。"
        );
    }
}
