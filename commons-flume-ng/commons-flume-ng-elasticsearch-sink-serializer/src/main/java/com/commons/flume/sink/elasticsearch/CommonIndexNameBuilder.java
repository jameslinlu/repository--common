package com.commons.flume.sink.elasticsearch;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.conf.ComponentConfiguration;
import org.apache.flume.sink.elasticsearch.ElasticSearchSinkConstants;
import org.apache.flume.sink.elasticsearch.IndexNameBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 使用原生index名称 不做改变
 * flume默认会使用TimeBasedIndexNameBuilder每天生成一个索引
 * 可扩展接受参数  配置不同规则的index名称
 * Copyright (C)
 * CommonIndexNameBuilder
 * Author: jameslinlu
 * <p/>
 * Deprecated 因为flume根本用不到他。IndexRequestBuilder已经解决问题
 * 以前这个应该是配合ElasticSearchEventSerializer impl使用的
 */
public class CommonIndexNameBuilder implements IndexNameBuilder {

    private static final Logger logger = LoggerFactory.getLogger(CommonElasticSearchIndexRequestBuilderFactory.class);

    private String indexName;

    @Override
    public String getIndexName(Event event) {
        return indexName;
    }

    @Override
    public String getIndexPrefix(Event event) {
        return indexName;
    }

    @Override
    public void configure(Context context) {
        logger.debug("config param  {}", context.getParameters().toString());
        indexName = context.getString(ElasticSearchSinkConstants.INDEX_NAME);
        logger.debug("config index name  {}", indexName);
    }

    @Override
    public void configure(ComponentConfiguration componentConfiguration) {

    }
}
