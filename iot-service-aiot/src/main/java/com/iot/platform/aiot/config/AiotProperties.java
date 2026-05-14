package com.iot.platform.aiot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 大模型提供方相关配置（可由 Nacos 或环境变量覆盖）。
 */
@Data
@ConfigurationProperties(prefix = "iot.aiot")
public class AiotProperties {

    /**
     * 请求未指定时使用的默认提供方。
     */
    private Provider defaultProvider = Provider.QWEN;

    /**
     * 是否在服务端自动执行模型生成的 SQL。
     */
    private boolean autoExecuteQuery = true;

    /**
     * 返回前端的最大行数（总结上下文亦受此约束）。
     */
    private int maxRows = 200;

    /**
     * 各提供方的 OpenAI 兼容对话配置。
     */
    private Providers providers = new Providers();

    public enum Provider {
        QWEN,
        DEEPSEEK
    }

    @Data
    public static class Providers {
        private OpenAiCompatible qwen = OpenAiCompatible.qwenDefaults();
        private OpenAiCompatible deepseek = OpenAiCompatible.deepseekDefaults();
    }

    @Data
    public static class OpenAiCompatible {
        /**
         * 基础 URL（不含末尾 /chat/completions）。示例：
         * 千问：https://dashscope.aliyuncs.com/compatible-mode/v1
         * DeepSeek：https://api.deepseek.com/v1
         */
        private String baseUrl;

        /**
         * API 密钥（建议环境变量或 Nacos，勿提交仓库）。
         */
        private String apiKey;

        /**
         * 默认对话模型 ID。示例：
         * 千问：qwen2.5-7b-instruct、qwen-max 等
         * DeepSeek：deepseek-chat、deepseek-reasoner 等
         */
        private String model;

        /**
         * 调用提供方接口的超时时间（毫秒）。
         */
        private int timeoutMs = 60000;

        public static OpenAiCompatible qwenDefaults() {
            OpenAiCompatible c = new OpenAiCompatible();
            c.baseUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1";
            c.apiKey = "";
            c.model = "qwen2.5-7b-instruct";
            return c;
        }

        public static OpenAiCompatible deepseekDefaults() {
            OpenAiCompatible c = new OpenAiCompatible();
            c.baseUrl = "https://api.deepseek.com/v1";
            c.apiKey = "";
            c.model = "deepseek-chat";
            return c;
        }
    }
}
