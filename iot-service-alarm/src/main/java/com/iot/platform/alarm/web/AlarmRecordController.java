package com.iot.platform.alarm.web;

import com.iot.platform.alarm.domain.AlarmRecord;
import com.iot.platform.alarm.repo.AlarmRecordRepository;
import com.iot.platform.common.api.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
public class AlarmRecordController {

    private final AlarmRecordRepository alarmRecordRepository;

    @GetMapping
    public ApiResult<List<AlarmRecord>> list(@RequestParam(required = false) Long ruleId) {
        if (ruleId != null) {
            return ApiResult.ok(alarmRecordRepository.findByRuleIdOrderByTriggeredAtDesc(ruleId));
        }
        return ApiResult.ok(alarmRecordRepository.findAll());
    }

    @GetMapping("/{id}")
    public ApiResult<AlarmRecord> get(@PathVariable Long id) {
        return ApiResult.ok(alarmRecordRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("记录不存在")));
    }
}
