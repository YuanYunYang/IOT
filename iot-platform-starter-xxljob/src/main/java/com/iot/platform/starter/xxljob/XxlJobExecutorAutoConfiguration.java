package com.iot.platform.starter.xxljob;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(XxlJobSpringExecutor.class)
@Conditional(XxlJobExecutorOnReadyCondition.class)
@EnableConfigurationProperties(IotXxlJobProperties.class)
public class XxlJobExecutorAutoConfiguration {

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor(IotXxlJobProperties p) {
        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        executor.setAdminAddresses(p.getAdminAddresses());
        executor.setAccessToken(p.getAccessToken());
        executor.setAppname(p.getAppname());
        executor.setAddress(p.getAddress());
        executor.setIp(p.getIp());
        executor.setPort(p.getPort());
        executor.setLogPath(p.getLogPath());
        executor.setLogRetentionDays(p.getLogRetentionDays());
        return executor;
    }
}
