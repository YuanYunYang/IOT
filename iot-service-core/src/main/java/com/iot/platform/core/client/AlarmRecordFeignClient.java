package com.iot.platform.core.client;

import com.iot.platform.common.api.ApiResult;
import com.iot.platform.core.client.dto.AlarmRecordViewDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "iot-service-alarm", path = "/api/v1/records")
public interface AlarmRecordFeignClient {

    @GetMapping
    ApiResult<List<AlarmRecordViewDto>> listRecords(@RequestParam(value = "ruleId", required = false) Long ruleId);

    @GetMapping("/{id}")
    ApiResult<AlarmRecordViewDto> getRecord(@PathVariable("id") Long id);
}
