package com.iot.platform.aiot.web;

import com.iot.platform.aiot.config.AiotProperties;
import com.iot.platform.aiot.kb.dto.KbQaRequest;
import com.iot.platform.aiot.kb.dto.KbQaResponse;
import com.iot.platform.aiot.kb.KbQaService;
import com.iot.platform.common.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 知识库 RAG 问答：经网关访问路径为 {@code /api/v1/aiot/kb/qa}。
 */
@RestController
@RequestMapping("/api/v1/kb")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "iot.aiot.kb.milvus", name = "enabled", havingValue = "true")
@Tag(name = "知识库问答", description = "按租户与门店权限从 Milvus 检索并调用大模型作答")
public class KbQaController {

    private final KbQaService kbQaService;

    @PostMapping("/qa")
    @Operation(summary = "知识库问答", description = "storeIds 为空则检索当前用户全部可访问门店；非空则与权限求交。需 Bearer JWT。")
    public ApiResult<KbQaResponse> qa(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody KbQaRequest request) {
        return ApiResult.ok(kbQaService.qa(
                authorization,
                request.getMessage(),
                request.getStoreIds(),
                request.getProvider(),
                request.getModel()));
    }
}
