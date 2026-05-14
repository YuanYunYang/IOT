package com.iot.platform.aiot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 知识库：Milvus 存储向量与文本；Embedding 与对话模型可同源（如 DashScope）。
 */
@Data
@ConfigurationProperties(prefix = "iot.aiot.kb")
public class KbProperties {

    private Milvus milvus = new Milvus();
    private Embedding embedding = new Embedding();

    @Data
    public static class Milvus {
        /** 为 true 时注册 Milvus 客户端、启动时建 collection、开放 /kb 接口 */
        private boolean enabled = false;
        private String host = "127.0.0.1";
        private int port = 19530;
        /** Collection 名，单 collection + 标量过滤 tenant_id / store_id */
        private String collection = "iot_kb_chunk";
        /** 与 embedding 模型输出维度一致，如 text-embedding-v1 为 1536 */
        private int vectorDim = 1536;
        private int topK = 8;
    }

    @Data
    public static class Embedding {
        /** OpenAI 兼容地址，如 https://dashscope.aliyuncs.com/compatible-mode/v1 */
        private String baseUrl = "";
        private String apiKey = "";
        private String model = "text-embedding-v1";
        private int timeoutMs = 60000;
    }
}
