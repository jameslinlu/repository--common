package com.commons.index.es;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Copyright @ 2016 zhong-ying Co.Ltd
 * All right reserved.
 * Function：ElasticSearch CleanUp
 *
 * @author jameslinlu
 */
public class ElasticSearchCleanUp {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchCleanUp.class);

    /**
     * 删除整个Index
     *
     * @param client
     * @param needDeletedIndex
     * @return
     */
    public static boolean deleteIndex(Client client, String needDeletedIndex) {
        return client.admin().indices().delete(new DeleteIndexRequest(needDeletedIndex))
                .actionGet().isAcknowledged();
    }

    /**
     * 删除 检索结果数据
     *
     * @param client
     * @param indexName
     * @param typeName
     * @param overdueDate
     * @return
     */
    public static boolean deleteIndex(Client client, String indexName, String typeName, String dateField, Date overdueDate) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.rangeQuery(dateField).lt(overdueDate.getTime()));
        return deleteIndex(client, indexName, typeName, queryBuilder);
    }


    /**
     * 依据 queryBuilder，删除 检索结果数据
     *
     * @param client
     * @param indexName
     * @param typeName
     * @param queryBuilder
     * @return
     */
    public static boolean deleteIndex(Client client, String indexName, String typeName, QueryBuilder queryBuilder) {
        TimeValue timeValue = new TimeValue(1000 * 60);
        SearchResponse scrollResp = client.prepareSearch(indexName).setTypes(typeName)
                .setScroll(timeValue)
                .setQuery(queryBuilder)
                .setSize(1000).execute().actionGet();

        BulkRequestBuilder bulkRequestBuilder;
        BulkResponse responses;
        boolean isAllSuccess = true;
        while (scrollResp.getHits().getHits().length > 0) {
            bulkRequestBuilder = client.prepareBulk();

            for (SearchHit searchHit : scrollResp.getHits().getHits()) {
                bulkRequestBuilder.add(client.prepareDelete(searchHit.getIndex(), searchHit.getType(), searchHit.getId()));
            }
            if (bulkRequestBuilder.numberOfActions() > 0) {
                responses = bulkRequestBuilder.execute().actionGet();
                if (responses.hasFailures()) {
                    isAllSuccess = false;
                    logger.error("deleteTraceFromES has failures,{}", responses.buildFailureMessage());
                }
                logger.debug("deleteTraceFromES spend millis {}", responses.getTookInMillis());
            }
            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(timeValue).execute().actionGet();
        }
        return isAllSuccess;
    }
}
