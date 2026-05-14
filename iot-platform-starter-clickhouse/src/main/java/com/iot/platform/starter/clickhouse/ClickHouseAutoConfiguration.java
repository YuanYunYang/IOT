package com.iot.platform.starter.clickhouse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Configuration
@ConditionalOnClass(java.sql.Driver.class)
@Conditional(ClickHouseOnReadyCondition.class)
@EnableConfigurationProperties(IotClickHouseProperties.class)
public class ClickHouseAutoConfiguration {

    @Bean
    public ClickHouseHelper clickHouseHelper(IotClickHouseProperties properties) {
        return new ClickHouseHelper(properties);
    }
}
