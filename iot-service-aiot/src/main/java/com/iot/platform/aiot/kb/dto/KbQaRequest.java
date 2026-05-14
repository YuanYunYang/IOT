package com.iot.platform.aiot.kb.dto;

import com.iot.platform.aiot.config.AiotProperties;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class KbQaRequest {

    @NotBlank
    private String message;

    /**
     * 为空则检索当前用户在 {@code sys_user_store} 下<strong>全部</strong>可访问门店的知识库；
     * 非空则与可访问门店求交，用于总部只查部分门店或单店场景。
     */
    private List<Long> storeIds;

    private AiotProperties.Provider provider;
    private String model;
}
