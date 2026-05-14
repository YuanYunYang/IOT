package com.iot.platform.alarm.web;

import com.iot.platform.alarm.domain.AlarmRule;
import com.iot.platform.alarm.dto.AlarmRuleRequest;
import com.iot.platform.alarm.service.AlarmRuleAdminService;
import com.iot.platform.common.api.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rules")
@RequiredArgsConstructor
public class AlarmRuleController {

    private final AlarmRuleAdminService alarmRuleAdminService;

    @GetMapping
    public ApiResult<List<AlarmRule>> list() {
        return ApiResult.ok(alarmRuleAdminService.listAll());
    }

    @GetMapping("/{id}")
    public ApiResult<AlarmRule> get(@PathVariable Long id) {
        return ApiResult.ok(alarmRuleAdminService.get(id));
    }

    @PostMapping
    public ApiResult<AlarmRule> create(@Valid @RequestBody AlarmRuleRequest req) {
        return ApiResult.ok(alarmRuleAdminService.create(req));
    }

    @PutMapping("/{id}")
    public ApiResult<AlarmRule> update(@PathVariable Long id, @Valid @RequestBody AlarmRuleRequest req) {
        return ApiResult.ok(alarmRuleAdminService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        alarmRuleAdminService.delete(id);
        return ApiResult.ok(null);
    }
}
