package com.iot.platform.aiot.kb.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class KbIngestRequest {

    @NotNull
    private Long storeId;

    @NotNull
    private Long docId;

    @NotBlank
    private String title;

    @NotEmpty
    private List<String> chunks;
}
