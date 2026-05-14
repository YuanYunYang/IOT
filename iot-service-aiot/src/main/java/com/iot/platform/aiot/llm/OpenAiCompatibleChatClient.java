package com.iot.platform.aiot.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.platform.aiot.config.AiotProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Objects;

/**
 * OpenAI 兼容对话客户端；基础 URL 按各厂商配置。
 */
@Component
public class OpenAiCompatibleChatClient {

    private final AiotProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OpenAiCompatibleChatClient(AiotProperties properties, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.properties = properties;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String chat(AiotProperties.Provider provider,
                       String modelOverride,
                       OpenAiChatDtos.ChatCompletionRequest request) {
        AiotProperties.OpenAiCompatible cfg = resolve(provider);
        String model = modelOverride != null && !modelOverride.trim().isEmpty() ? modelOverride : cfg.getModel();
        request.setModel(model);

        String url = normalizeBaseUrl(cfg.getBaseUrl()) + "/chat/completions";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
        if (cfg.getApiKey() != null && !cfg.getApiKey().trim().isEmpty()) {
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + cfg.getApiKey().trim());
        }
        HttpEntity<OpenAiChatDtos.ChatCompletionRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<OpenAiChatDtos.ChatCompletionResponse> resp = restTemplate.postForEntity(
                    URI.create(url),
                    entity,
                    OpenAiChatDtos.ChatCompletionResponse.class
            );
            OpenAiChatDtos.ChatCompletionResponse body = resp.getBody();
            if (body == null || body.getChoices() == null || body.getChoices().isEmpty()) {
                throw new IllegalStateException("大模型响应缺少 choices");
            }
            OpenAiChatDtos.Message msg = body.getChoices().get(0).getMessage();
            if (msg == null || msg.getContent() == null) {
                throw new IllegalStateException("大模型回复正文为空");
            }
            return msg.getContent();
        } catch (RestClientException e) {
            throw new IllegalStateException("调用大模型失败：" + provider + " url=" + url + " err=" + e.getMessage(), e);
        }
    }

    public <T> T readJson(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            throw new IllegalArgumentException("解析大模型 JSON 失败：" + e.getMessage() + "。原始片段=" + safePreview(json), e);
        }
    }

    private AiotProperties.OpenAiCompatible resolve(AiotProperties.Provider provider) {
        Objects.requireNonNull(provider, "provider");
        if (provider == AiotProperties.Provider.DEEPSEEK) {
            return properties.getProviders().getDeepseek();
        }
        return properties.getProviders().getQwen();
    }

    private static String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null) {
            return "";
        }
        String s = baseUrl.trim();
        while (s.endsWith("/")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    private static String safePreview(String s) {
        if (s == null) {
            return "null";
        }
        int max = 600;
        return s.length() <= max ? s : s.substring(0, max) + "...(truncated)";
    }
}

