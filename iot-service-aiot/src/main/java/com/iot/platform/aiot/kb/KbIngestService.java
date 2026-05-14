package com.iot.platform.aiot.kb;

import com.iot.platform.aiot.config.KbProperties;
import com.iot.platform.aiot.kb.KbScopeResolver.KbScope;
import com.iot.platform.aiot.kb.dto.KbIngestRequest;
import com.iot.platform.aiot.kb.milvus.KbEmbeddingClient;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.R;
import io.milvus.param.collection.FlushParam;
import io.milvus.param.dml.InsertParam;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 将切块写入 Milvus（仅存向量与标量，不写 MySQL）。
 */
@Service
@ConditionalOnProperty(prefix = "iot.aiot.kb.milvus", name = "enabled", havingValue = "true")
public class KbIngestService {

    private static final int MAX_CHUNK_CHARS = 16000;

    private final KbScopeResolver kbScopeResolver;
    private final MilvusServiceClient milvusClient;
    private final KbProperties kbProperties;
    private final KbEmbeddingClient embeddingClient;

    public KbIngestService(KbScopeResolver kbScopeResolver,
                           MilvusServiceClient milvusClient,
                           KbProperties kbProperties,
                           KbEmbeddingClient embeddingClient) {
        this.kbScopeResolver = kbScopeResolver;
        this.milvusClient = milvusClient;
        this.kbProperties = kbProperties;
        this.embeddingClient = embeddingClient;
    }

    public int ingest(String authorizationHeader, KbIngestRequest req) {
        KbScope scope = kbScopeResolver.resolve(authorizationHeader, Collections.singletonList(req.getStoreId()));
        long tenantId = scope.getTenantId();
        long storeId = req.getStoreId();
        List<String> chunks = req.getChunks();
        int n = chunks.size();

        List<Long> tenantIds = new ArrayList<>(n);
        List<Long> storeIds = new ArrayList<>(n);
        List<Long> docIds = new ArrayList<>(n);
        List<Integer> chunkIndices = new ArrayList<>(n);
        List<String> titles = new ArrayList<>(n);
        List<String> texts = new ArrayList<>(n);
        List<List<Float>> vectors = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            String raw = chunks.get(i) == null ? "" : chunks.get(i);
            if (raw.length() > MAX_CHUNK_CHARS) {
                raw = raw.substring(0, MAX_CHUNK_CHARS);
            }
            tenantIds.add(tenantId);
            storeIds.add(storeId);
            docIds.add(req.getDocId());
            chunkIndices.add(i);
            titles.add(req.getTitle());
            texts.add(raw);
            vectors.add(embeddingClient.embed(raw));
        }

        String col = kbProperties.getMilvus().getCollection();
        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field("tenant_id", tenantIds));
        fields.add(new InsertParam.Field("store_id", storeIds));
        fields.add(new InsertParam.Field("doc_id", docIds));
        fields.add(new InsertParam.Field("chunk_index", chunkIndices));
        fields.add(new InsertParam.Field("title", titles));
        fields.add(new InsertParam.Field("chunk_text", texts));
        fields.add(new InsertParam.Field("embedding", vectors));

        InsertParam insert = InsertParam.newBuilder()
                .withCollectionName(col)
                .withFields(fields)
                .build();
        R<?> ir = milvusClient.insert(insert);
        if (ir.getStatus() != R.Status.Success.getCode()) {
            throw new IllegalStateException("Milvus insert: " + ir.getMessage());
        }
        R<?> fr = milvusClient.flush(FlushParam.newBuilder().addCollectionName(col).build());
        if (fr.getStatus() != R.Status.Success.getCode()) {
            throw new IllegalStateException("Milvus flush: " + fr.getMessage());
        }
        return n;
    }
}
