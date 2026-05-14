package com.iot.platform.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.iot.platform.core.client")
public class IotCoreServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotCoreServiceApplication.class, args);
    }
}
