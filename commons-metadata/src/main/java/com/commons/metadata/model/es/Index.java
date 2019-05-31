package com.commons.metadata.model.es;

/**
 * Copyright (C).
 * Administrator
 * Author
 */
public class Index {
    private String clusterName;//集群名称
    private String indexName;//索引名称 op_logger_v1
    private String typeName;//根基indexName处理后赋值

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }
}
