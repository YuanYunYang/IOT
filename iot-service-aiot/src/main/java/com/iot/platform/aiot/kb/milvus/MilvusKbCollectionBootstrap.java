package com.iot.platform.aiot.kb.milvus;

import com.iot.platform.aiot.config.KbProperties;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.param.R;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.collection.HasCollectionParam;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 启动时若 collection 不存在则创建并建索引、加载；向量维度与 {@link KbProperties.Milvus#vectorDim} 一致。
 */
@Component
@Order(30)
@ConditionalOnProperty(prefix = "iot.aiot.kb.milvus", name = "enabled", havingValue = "true")
public class MilvusKbCollectionBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(MilvusKbCollectionBootstrap.class);

    private final MilvusServiceClient milvusClient;
    private final KbProperties kbProperties;

    public MilvusKbCollectionBootstrap(MilvusServiceClient milvusClient, KbProperties kbProperties) {
        this.milvusClient = milvusClient;
        this.kbProperties = kbProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        String col = kbProperties.getMilvus().getCollection();
        int dim = kbProperties.getMilvus().getVectorDim();

        R<Boolean> hasR = milvusClient.hasCollection(HasCollectionParam.newBuilder().withCollectionName(col).build());
        assertOk(hasR, "hasCollection");
        if (Boolean.TRUE.equals(hasR.getData())) {
            loadCollection(col);
            log.info("Milvus 知识库 collection 已存在并已加载: {}", col);
            return;
        }

        List<FieldType> fields = new ArrayList<>();
        fields.add(FieldType.newBuilder()
                .withName("id")
                .withDataType(DataType.Int64)
                .withPrimaryKey(true)
                .withAutoID(true)
                .build());
        fields.add(FieldType.newBuilder().withName("tenant_id").withDataType(DataType.Int64).build());
        fields.add(FieldType.newBuilder().withName("store_id").withDataType(DataType.Int64).build());
        fields.add(FieldType.newBuilder().withName("doc_id").withDataType(DataType.Int64).build());
        fields.add(FieldType.newBuilder().withName("chunk_index").withDataType(DataType.Int32).build());
        fields.add(FieldType.newBuilder()
                .withName("title")
                .withDataType(DataType.VarChar)
                .withMaxLength(512)
                .build());
        fields.add(FieldType.newBuilder()
                .withName("chunk_text")
                .withDataType(DataType.VarChar)
                .withMaxLength(16384)
                .build());
        fields.add(FieldType.newBuilder()
                .withName("embedding")
                .withDataType(DataType.FloatVector)
                .withDimension(dim)
                .build());

        CreateCollectionParam create = CreateCollectionParam.newBuilder()
                .withCollectionName(col)
                .withDescription("IoT KB chunks; filter by tenant_id + store_id")
                .withFieldTypes(fields)
                .build();
        R<?> cr = milvusClient.createCollection(create);
        assertOk(cr, "createCollection");

        CreateIndexParam index = CreateIndexParam.newBuilder()
                .withCollectionName(col)
                .withFieldName("embedding")
                .withIndexType(IndexType.IVF_FLAT)
                .withMetricType(MetricType.COSINE)
                .withExtraParam("{\"nlist\":1024}")
                .build();
        R<?> ir = milvusClient.createIndex(index);
        assertOk(ir, "createIndex");

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        loadCollection(col);
        log.info("Milvus 知识库 collection 已创建并加载: {} dim={}", col, dim);
    }

    private void loadCollection(String col) {
        R<?> lr = milvusClient.loadCollection(LoadCollectionParam.newBuilder().withCollectionName(col).build());
        assertOk(lr, "loadCollection");
    }

    private static void assertOk(R<?> r, String op) {
        if (r == null) {
            throw new IllegalStateException("Milvus " + op + ": null response");
        }
        if (r.getStatus() != R.Status.Success.getCode()) {
            throw new IllegalStateException("Milvus " + op + " 失败: " + r.getMessage());
        }
    }
}
