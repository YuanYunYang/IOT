package com.iot.platform.device;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.iot.platform.device.cache.DeviceMetadataCacheProperties;

@SpringBootApplication
@EnableConfigurationProperties(DeviceMetadataCacheProperties.class)
public class IotDeviceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotDeviceServiceApplication.class, args);
    }
}
