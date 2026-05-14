package com.iot.platform.aiot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.iot.platform.aiot.client")
public class IotAiotServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotAiotServiceApplication.class, args);
    }
}
