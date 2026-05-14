package com.iot.platform.aiot.llm;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * OpenAI 兼容「对话补全」接口的最小 DTO；适用于通义千问兼容模式、DeepSeek 等厂商。
 */
public final class OpenAiChatDtos {

    private OpenAiChatDtos() {
    }

    @Data
    public static class ChatCompletionRequest {
        private String model;
        private List<Message> messages;
        private Double temperature;
        private Integer max_tokens;
        /**
         * 可选：请求服务端返回 JSON 对象（OpenAI 兼容字段）；厂商可能忽略，仍会通过提示词约束 JSON。
         */
        private Map<String, Object> response_format;
    }

    @Data
    public static class Message {
        /** 角色：system / user / assistant */
        private String role;
        private String content;

        public static Message system(String content) {
            Message m = new Message();
            m.role = "system";
            m.content = content;
            return m;
        }

        public static Message user(String content) {
            Message m = new Message();
            m.role = "user";
            m.content = content;
            return m;
        }
    }

    @Data
    public static class ChatCompletionResponse {
        private List<Choice> choices;
        private Usage usage;
        private String model;
    }

    @Data
    public static class Choice {
        private int index;
        private Message message;
        private String finish_reason;
    }

    @Data
    public static class Usage {
        private Integer prompt_tokens;
        private Integer completion_tokens;
        private Integer total_tokens;
    }
}

