package com.iot.platform.starter.clickhouse;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/**
 * iot.clickhouse.enabled 为 true（缺省视为 true）且已配置非空 iot.clickhouse.url。
 */
public class ClickHouseOnReadyCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        boolean enabled = env.getProperty("iot.clickhouse.enabled", Boolean.class, Boolean.TRUE);
        if (!enabled) {
            return false;
        }
        return StringUtils.hasText(env.getProperty("iot.clickhouse.url"));
    }
}
