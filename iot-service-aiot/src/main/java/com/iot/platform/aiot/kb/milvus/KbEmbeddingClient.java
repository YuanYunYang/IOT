package com.iot.platform.aiot.kb.milvus;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.platform.aiot.config.KbProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * OpenAI 兼容 /v1/embeddings，与 DashScope 兼容模式等对齐。
 */
@Component
@ConditionalOnProperty(prefix = "iot.aiot.kb.milvus", name = "enabled", havingValue = "true")
public class KbEmbeddingClient {

    private final KbProperties kbProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public KbEmbeddingClient(KbProperties kbProperties, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.kbProperties = kbProperties;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<Float> embed(String input) {
        KbProperties.Embedding e = kbProperties.getEmbedding();
        if (!StringUtils.hasText(e.getBaseUrl()) || !StringUtils.hasText(e.getApiKey())) {
            throw new IllegalStateException("请配置 iot.aiot.kb.embedding.base-url 与 api-key");
        }
        String base = e.getBaseUrl().trim();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        String url = base + "/embeddings";

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", e.getModel());
        payload.put("input", input);
        String body;
        try {
            body = objectMapper.writeValueAsString(payload);
        } catch (Exception ex) {
            throw new IllegalStateException("构造 Embedding 请求失败", ex);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(e.getApiKey().trim());

        ResponseEntity<JsonNode> resp = restTemplate.postForEntity(url, new HttpEntity<>(body, headers), JsonNode.class);
        JsonNode root = resp.getBody();
        if (root == null || !root.has("data") || !root.get("data").isArray() || root.get("data").size() == 0) {
            throw new IllegalStateException("Embedding 响应异常");
        }
        JsonNode emb = root.get("data").get(0).get("embedding");
        if (emb == null || !emb.isArray()) {
            throw new IllegalStateException("Embedding 向量缺失");
        }
        List<Float> vec = new ArrayList<>();
        Iterator<JsonNode> it = emb.elements();
        while (it.hasNext()) {
            vec.add((float) it.next().asDouble());
        }
        int dim = kbProperties.getMilvus().getVectorDim();
        if (vec.size() != dim) {
            throw new IllegalStateException("向量维度与 iot.aiot.kb.milvus.vector-dim 不一致: 实际=" + vec.size() + " 配置=" + dim);
        }
        return vec;
    }
}
