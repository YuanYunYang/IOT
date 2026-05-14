package com.iot.platform.alarm.repo;

import com.iot.platform.alarm.domain.AlarmRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRecordRepository extends JpaRepository<AlarmRecord, Long> {

    List<AlarmRecord> findByRuleIdOrderByTriggeredAtDesc(Long ruleId);
}
