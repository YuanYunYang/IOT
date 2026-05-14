package com.iot.platform.core.job;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

@Component
public class SampleXxlJobHandler {

    @XxlJob("sampleJob")
    public void sampleJob() {
        XxlJobHelper.log("iot-service-core sampleJob executed");
    }
}
