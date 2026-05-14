package com.iot.platform.aiot.kb.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class KbQaResponse {

    private String reply;
    /** 本次检索实际使用的门店 ID（已按权限裁剪） */
    private List<Long> usedStoreIds;
    private int contextChunkCount;
    private String provider;
    private String model;
}
