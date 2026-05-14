package com.iot.platform.aiot.kb;

import com.iot.platform.aiot.config.AiotProperties;
import com.iot.platform.aiot.kb.model.KbChunkHit;
import com.iot.platform.aiot.kb.milvus.KbMilvusChunkSearchRepository;
import com.iot.platform.aiot.kb.KbScopeResolver.KbScope;
import com.iot.platform.aiot.kb.dto.KbQaResponse;
import com.iot.platform.aiot.llm.OpenAiChatDtos;
import com.iot.platform.aiot.llm.OpenAiCompatibleChatClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 知识库问答：权限范围来自用户服务 session；检索按 tenant + store；答案由大模型基于检索片段生成。
 */
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "iot.aiot.kb.milvus", name = "enabled", havingValue = "true")
public class KbQaService {

    private final KbScopeResolver kbScopeResolver;
    private final KbMilvusChunkSearchRepository kbMilvusChunkSearchRepository;
    private final OpenAiCompatibleChatClient chatClient;
    private final AiotProperties aiotProperties;

    public KbQaResponse qa(String authorizationHeader, String userMessage, List<Long> requestedStoreIds,
                           AiotProperties.Provider provider, String modelOverride) {
        KbScope scope = kbScopeResolver.resolve(authorizationHeader, requestedStoreIds);
        List<KbChunkHit> hits = kbMilvusChunkSearchRepository.search(scope.getTenantId(), scope.getStoreIds(), userMessage);
        String context = buildContext(hits);
        AiotProperties.Provider p = provider != null ? provider : aiotProperties.getDefaultProvider();

        String system = "你是企业知识库问答助手。请**仅根据**下面「参考资料」回答用户问题；"
                + "资料不足以回答时请明确说明，并不要编造事实。"
                + "回答使用中文，简洁有条理。";
        String userBlock = "参考资料（含门店维度，store_id 为门店标识）：\n" + context + "\n\n用户问题：\n" + userMessage;

        OpenAiChatDtos.ChatCompletionRequest req = new OpenAiChatDtos.ChatCompletionRequest();
        req.setTemperature(0.2);
        req.setMessages(java.util.Arrays.asList(
                OpenAiChatDtos.Message.system(system),
                OpenAiChatDtos.Message.user(userBlock)
        ));
        String reply = chatClient.chat(p, modelOverride, req).trim();

        return KbQaResponse.builder()
                .reply(reply)
                .usedStoreIds(new ArrayList<>(scope.getStoreIds()))
                .contextChunkCount(hits.size())
                .provider(p.name())
                .model(resolveModel(p, modelOverride))
                .build();
    }

    private String resolveModel(AiotProperties.Provider provider, String modelOverride) {
        if (modelOverride != null && !modelOverride.trim().isEmpty()) {
            return modelOverride.trim();
        }
        if (provider == AiotProperties.Provider.DEEPSEEK) {
            return aiotProperties.getProviders().getDeepseek().getModel();
        }
        return aiotProperties.getProviders().getQwen().getModel();
    }

    private static String buildContext(List<KbChunkHit> hits) {
        if (hits.isEmpty()) {
            return "（无匹配片段；可能知识库尚无内容或关键词未命中。）";
        }
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (KbChunkHit h : hits) {
            sb.append("[").append(i++).append("] store_id=").append(h.getStoreId())
                    .append(" doc=").append(h.getTitle())
                    .append("\n").append(h.getContent()).append("\n\n");
        }
        return sb.toString();
    }
}
