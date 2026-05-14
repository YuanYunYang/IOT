package com.iot.platform.aiot.web;

import com.iot.platform.aiot.kb.KbIngestService;
import com.iot.platform.aiot.kb.dto.KbIngestRequest;
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
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/kb")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "iot.aiot.kb.milvus", name = "enabled", havingValue = "true")
@Tag(name = "知识库写入", description = "向 Milvus 写入切块；需 Bearer，且 storeId 须在用户可访问门店内")
public class KbIngestController {

    private final KbIngestService kbIngestService;

    @PostMapping("/ingest")
    @Operation(summary = "写入知识切块", description = "按当前租户与指定门店写入向量与文本。")
    public ApiResult<Map<String, Object>> ingest(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody KbIngestRequest request) {
        int n = kbIngestService.ingest(authorization, request);
        return ApiResult.ok(Collections.singletonMap("insertedChunks", n));
    }
}
