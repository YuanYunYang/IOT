package com.iot.platform.aiot.web;

import com.iot.platform.aiot.config.AiotProperties;
import com.iot.platform.aiot.service.AiChatService;
import com.iot.platform.aiot.web.dto.NlDataChatResponse;
import com.iot.platform.common.api.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

/**
 * REST：AI 报表、能耗预测、中文自然语言查数（当前为规则 + 数据字典；可换 LLM）。
 */
@RestController
@RequestMapping("/api/v1")
public class AiotApiController {

    private final AiChatService aiChatService;
    private final AiotProperties properties;

    public AiotApiController(AiChatService aiChatService, AiotProperties properties) {
        this.aiChatService = aiChatService;
        this.properties = properties;
    }

    @GetMapping("/reports/ai")
    public ApiResult<Map<String, Object>> aiReport() {
        Map<String, Object> body = new HashMap<>();
        body.put("status", "stub");
        body.put("message", "Wire ClickHouse / MySQL queries and LLM summarization here.");
        return ApiResult.ok(body);
    }

    @GetMapping("/energy/prediction")
    public ApiResult<Map<String, Object>> energyPrediction() {
        Map<String, Object> body = new HashMap<>();
        body.put("status", "stub");
        body.put("message", "Wire historical series from CK/Redis and forecasting model here.");
        return ApiResult.ok(body);
    }

    /**
     * 网页端输入中文描述：LLM 生成 SQL -> 服务端只读校验 -> 自动执行 -> LLM 总结。
     */
    @PostMapping("/chat")
    public ApiResult<NlDataChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        AiotProperties.Provider provider = request.getProvider() != null ? request.getProvider() : properties.getDefaultProvider();
        return ApiResult.ok(aiChatService.chat(provider, request.getModel(), request.getMessage()));
    }

    public static class ChatRequest {
        @NotBlank
        private String message;

        /**
         * 可选：提供方，QWEN 或 DEEPSEEK。
         */
        private AiotProperties.Provider provider;

        /**
         * 可选：覆盖默认模型 ID。
         */
        private String model;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public AiotProperties.Provider getProvider() {
            return provider;
        }

        public void setProvider(AiotProperties.Provider provider) {
            this.provider = provider;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }
    }
}
