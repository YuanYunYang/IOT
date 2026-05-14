package com.iot.platform.aiot.kb.milvus;

import com.iot.platform.aiot.config.KbProperties;
import com.iot.platform.aiot.kb.model.KbChunkHit;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.dml.SearchParam;
import io.milvus.response.SearchResultsWrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Milvus 向量检索 + 标量过滤 tenant_id / store_id；不依赖业务库表存 chunk。
 */
@Component
@ConditionalOnProperty(prefix = "iot.aiot.kb.milvus", name = "enabled", havingValue = "true")
public class KbMilvusChunkSearchRepository {

    private final MilvusServiceClient milvusClient;
    private final KbProperties kbProperties;
    private final KbEmbeddingClient embeddingClient;

    public KbMilvusChunkSearchRepository(MilvusServiceClient milvusClient,
                                        KbProperties kbProperties,
                                        KbEmbeddingClient embeddingClient) {
        this.milvusClient = milvusClient;
        this.kbProperties = kbProperties;
        this.embeddingClient = embeddingClient;
    }

    public List<KbChunkHit> search(long tenantId, List<Long> storeIds, String queryText) {
        if (storeIds == null || storeIds.isEmpty()) {
            return Collections.emptyList();
        }
        String q = queryText == null ? "" : queryText.trim();
        if (q.isEmpty()) {
            return Collections.emptyList();
        }
        List<Float> vector = embeddingClient.embed(q.length() > 512 ? q.substring(0, 512) : q);
        List<List<Float>> vectors = Collections.singletonList(vector);

        String expr = buildExpr(tenantId, storeIds);
        String col = kbProperties.getMilvus().getCollection();
        int topK = Math.max(1, kbProperties.getMilvus().getTopK());

        SearchParam sp = SearchParam.newBuilder()
                .withCollectionName(col)
                .withVectorFieldName("embedding")
                .withMetricType(MetricType.COSINE)
                .withOutFields(java.util.Arrays.asList("store_id", "doc_id", "chunk_index", "title", "chunk_text"))
                .withTopK(topK)
                .withExpr(expr)
                .withVectors(vectors)
                .withParams("{\"nprobe\":16}")
                .build();

        R<io.milvus.response.SearchResults> resp = milvusClient.search(sp);
        if (resp.getStatus() != R.Status.Success.getCode()) {
            throw new IllegalStateException("Milvus search: " + resp.getMessage());
        }
        io.milvus.response.SearchResults data = resp.getData();
        if (data == null || data.getResults() == null) {
            return Collections.emptyList();
        }
        SearchResultsWrapper wrapper = new SearchResultsWrapper(data.getResults());
        List<?> idScores = wrapper.getIDScore(0);
        List<KbChunkHit> hits = new ArrayList<>();
        for (Object o : idScores) {
            SearchResultsWrapper.IDScore row = (SearchResultsWrapper.IDScore) o;
            KbChunkHit h = new KbChunkHit();
            h.setChunkId(row.getLongID());
            Object sid = row.get("store_id");
            h.setStoreId(sid instanceof Number ? ((Number) sid).longValue() : Long.parseLong(String.valueOf(sid)));
            Object did = row.get("doc_id");
            h.setDocumentId(did instanceof Number ? ((Number) did).longValue() : Long.parseLong(String.valueOf(did)));
            Object cidx = row.get("chunk_index");
            h.setChunkIndex(cidx instanceof Number ? ((Number) cidx).intValue() : Integer.parseInt(String.valueOf(cidx)));
            h.setTitle(String.valueOf(row.get("title")));
            h.setContent(String.valueOf(row.get("chunk_text")));
            hits.add(h);
        }
        return hits;
    }

    private static String buildExpr(long tenantId, List<Long> storeIds) {
        String inList = storeIds.stream().map(String::valueOf).collect(Collectors.joining(", "));
        return "tenant_id == " + tenantId + " && store_id in [" + inList + "]";
    }
}
