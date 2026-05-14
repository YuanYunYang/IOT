package com.iot.platform.alarm;

import com.iot.platform.alarm.config.IotAlarmProperties;
import com.iot.platform.alarm.mqtt.MqttProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({IotAlarmProperties.class, MqttProperties.class})
public class IotAlarmServiceApplication {
    

    public static void main(String[] args) {
        SpringApplication.run(IotAlarmServiceApplication.class, args);
    }
}
