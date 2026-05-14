package com.iot.platform.alarm.service;

import com.iot.platform.alarm.domain.AlarmRule;
import com.iot.platform.alarm.repo.AlarmRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmRuleEvaluationScheduler {

    private final AlarmRuleRepository ruleRepository;
    private final AlarmRuleEvaluator alarmRuleEvaluator;

    @Scheduled(fixedDelayString = "${iot.alarm.evaluate-interval-ms:10000}")
    public void evaluateAll() {
        List<AlarmRule> rules = ruleRepository.findByEnabledTrue();
        for (AlarmRule rule : rules) {
            try {
                alarmRuleEvaluator.evaluate(rule);
            } catch (Exception e) {
                log.warn("rule eval failed id={} name={}", rule.getId(), rule.getName(), e);
            }
        }
    }
}
