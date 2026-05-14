package com.iot.platform.device.web;

import com.iot.platform.common.api.ApiResult;
import com.iot.platform.starter.clickhouse.ClickHouseHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

/**
 * 示例：验证 ClickHouse 连通（需 iot.clickhouse.enabled=true 且账号正确）。
 */
@RestController
@RequestMapping("/api/v1/internal/clickhouse")
@RequiredArgsConstructor
@ConditionalOnBean(ClickHouseHelper.class)
public class ClickHouseProbeController {

    private final ClickHouseHelper clickHouseHelper;

    @GetMapping("/ping")
    public ApiResult<Map<String, Object>> ping() throws SQLException {
        Map<String, Object> row = clickHouseHelper.queryOneMap("SELECT 1 AS ok");
        return ApiResult.ok(row != null ? row : Collections.singletonMap("ok", 1));
    }
}
