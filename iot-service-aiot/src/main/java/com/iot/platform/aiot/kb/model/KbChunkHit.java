package com.iot.platform.aiot.kb.model;

import lombok.Data;

/**
 * 知识库检索命中的一条文本块（Milvus 标量字段 + 向量召回）。
 */
@Data
public class KbChunkHit {

    private long chunkId;
    private long documentId;
    private int chunkIndex;
    private String content;
    private String title;
    private long storeId;
}
