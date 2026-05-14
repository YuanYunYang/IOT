package com.iot.platform.alarm.repo;

import com.iot.platform.alarm.domain.AlarmRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRuleRepository extends JpaRepository<AlarmRule, Long> {

    List<AlarmRule> findByEnabledTrue();

    List<AlarmRule> findByEnabledTrueAndTelemetryRedisKey(String telemetryRedisKey);
}
