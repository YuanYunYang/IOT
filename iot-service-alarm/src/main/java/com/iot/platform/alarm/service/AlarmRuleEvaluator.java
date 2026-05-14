package com.iot.platform.alarm.service;

import com.iot.platform.alarm.config.IotAlarmProperties;
import com.iot.platform.alarm.domain.AlarmRecord;
import com.iot.platform.alarm.domain.AlarmRule;
import com.iot.platform.alarm.domain.CompareOperator;
import com.iot.platform.alarm.repo.AlarmRecordRepository;
import com.iot.platform.starter.redis.IotRedisHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AlarmRuleEvaluator {

    private final AlarmRecordRepository recordRepository;
    private final IotRedisHelper redisHelper;
    private final IotAlarmProperties alarmProperties;

    @Transactional
    public void evaluate(AlarmRule rule) {
        String prefix = alarmProperties.getStateKeyPrefix();
        String firstKey = prefix + rule.getId() + ":first";
        String firedKey = prefix + rule.getId() + ":fired";

        Optional<String> rawOpt = redisHelper.get(rule.getTelemetryRedisKey());
        String raw = rawOpt.orElse(null);
        double value = RuleCompareUtil.parseNumeric(raw);
        double threshold = RuleCompareUtil.parseNumeric(rule.getThresholdValue());
        CompareOperator op = rule.getCompareOperator();

        boolean match = RuleCompareUtil.compare(value, threshold, op) && !Double.isNaN(threshold);

        if (!match) {
            redisHelper.delete(firstKey);
            redisHelper.delete(firedKey);
            return;
        }

        int needMs = Math.max(0, rule.getDurationSeconds()) * 1000;
        long now = System.currentTimeMillis();

        if (needMs == 0) {
            if (Boolean.TRUE.equals(redisHelper.hasKey(firedKey))) {
                return;
            }
            saveRecord(rule, raw, op);
            redisHelper.set(firedKey, "1");
            return;
        }

        Optional<String> firstStr = redisHelper.get(firstKey);
        if (!firstStr.isPresent()) {
            redisHelper.set(firstKey, String.valueOf(now));
            return;
        }

        long first = Long.parseLong(firstStr.get());
        if (now - first < needMs) {
            return;
        }

        if (Boolean.TRUE.equals(redisHelper.hasKey(firedKey))) {
            return;
        }

        saveRecord(rule, raw, op);
        redisHelper.set(firedKey, "1");
    }

    private void saveRecord(AlarmRule rule, String raw, CompareOperator op) {
        AlarmRecord rec = new AlarmRecord();
        rec.setRuleId(rule.getId());
        rec.setDeviceId(rule.getDeviceId());
        rec.setTitle(rule.getAlarmTitle() != null ? rule.getAlarmTitle() : rule.getName());
        rec.setMessage(String.format("规则[%s] 点位值=%s 持续满足条件(阈值=%s %s)", rule.getName(), raw, rule.getThresholdValue(), op));
        rec.setAlarmLevel(rule.getAlarmLevel());
        rec.setPointValueSnapshot(raw);
        rec.setTriggeredAt(Instant.now());
        recordRepository.save(rec);
    }
}
