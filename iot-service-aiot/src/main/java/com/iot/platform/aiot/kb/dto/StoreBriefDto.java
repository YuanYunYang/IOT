package com.iot.platform.aiot.kb.dto;

import lombok.Data;

@Data
public class StoreBriefDto {

    private Long id;
    private String storeCode;
    private String storeName;
    private Boolean enabled;
}
