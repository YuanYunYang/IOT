package com.iot.platform.starter.xxljob;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/**
 * iot.xxl.job.enabled=true 且已配置非空的 admin-addresses 与 appname。
 */
public class XxlJobExecutorOnReadyCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        Boolean enabled = env.getProperty("iot.xxl.job.enabled", Boolean.class);
        if (enabled == null || !enabled) {
            return false;
        }
        return StringUtils.hasText(env.getProperty("iot.xxl.job.admin-addresses"))
                && StringUtils.hasText(env.getProperty("iot.xxl.job.appname"));
    }
}
