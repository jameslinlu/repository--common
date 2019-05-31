package com.commons.index.es.generic;

import com.commons.index.es.ElasticSearchManager;
import com.commons.metadata.model.es.Index;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 通用ES客户端支持
 * Copyright (C)
 * GenericEsSupport
 * Author: jameslinlu
 */
public abstract class GenericEsSupport {

    private static final Logger logger = LoggerFactory.getLogger(GenericEsSupport.class);


    @Autowired
    private ElasticSearchManager elasticSearchManager;

    protected TimeValue getTimeout() {
        return new TimeValue(1000 * 60);
    }

    protected Client getClient(Index index) {
        Client client = elasticSearchManager.get(index.getClusterName());
        return client;
    }

    protected SearchRequestBuilder getSearchRequestBuilder(Index index) {
        Client client = getClient(index);
        return client.prepareSearch(index.getIndexName()).setTypes(index.getTypeName());
    }
}
