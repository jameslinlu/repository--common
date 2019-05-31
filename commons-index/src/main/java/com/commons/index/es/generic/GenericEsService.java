package com.commons.index.es.generic;


import com.alibaba.fastjson.JSON;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.generic.IEsService;
import com.commons.metadata.model.Page;
import com.commons.metadata.model.PageParam;
import com.commons.metadata.model.PageWrapper;
import com.commons.metadata.model.es.Index;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用ES业务增删改查支持
 */
public abstract class GenericEsService<T> extends GenericEsSupport implements IEsService<T> {


    private static final Logger logger = LoggerFactory.getLogger(GenericEsService.class);

    /**
     * 获取查詢條件
     *
     * @param model
     * @return
     */
    public abstract QueryBuilder getQuery(T model, OperationType operationType);

    /**
     * 获取排序
     *
     * @param model
     * @param operationType
     * @return
     */
    public abstract SortBuilder getSort(T model, OperationType operationType);

    @Override
    public List<T> inserts(List<T> models, Index index) throws ServiceException {
        Client client = getClient(index);
        BulkRequestBuilder builder = client.prepareBulk();
        for (T model : models) {
            builder.add(
                    client.prepareIndex(index.getIndexName(), index.getTypeName())
                            .setOpType(DocWriteRequest.OpType.INDEX)
//                        .setCreate(true)
//                            .setContentType(XContentType.JSON)
                            .setSource(JSON.toJSONString(model), XContentType.JSON));
        }
//        BulkResponse res = builder.setRefresh(true).execute().actionGet();
        BulkResponse res = builder.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE).execute().actionGet();
        if (res != null && res.hasFailures()) {
            throw new ServiceException(ResultCode.ERROR_SAVE);
        }
        return models;
    }

    @Override
    public T insert(T model, Index index) throws ServiceException {
        Client client = getClient(index);
        BulkRequestBuilder builder = client.prepareBulk();
        builder.add(
                client.prepareIndex(index.getIndexName(), index.getTypeName())
                        .setOpType(DocWriteRequest.OpType.INDEX)
//                        .setCreate(true)
//                        .setContentType(XContentType.JSON)
                        .setSource(JSON.toJSONString(model), XContentType.JSON));
//        BulkResponse res = builder.setRefresh(true).execute().actionGet();
        BulkResponse res = builder.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE).execute().actionGet();
        if (res != null && res.hasFailures()) {
            throw new ServiceException(ResultCode.ERROR_SAVE);
        }
        return model;
    }

    @Override
    public boolean update(T model, Index index) throws ServiceException {
        try {
            SearchResponse response = this.get(model, index, OperationType.UPDATE);
            Client client = getClient(index);
            if (response.getHits().getTotalHits() == 1) {
                SearchHit hit = response.getHits().getAt(0);
                UpdateResponse result = client.prepareUpdate(hit.getIndex(), hit.getType(), hit.getId())
                        .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                        .setDoc(JSON.toJSONString(model), XContentType.JSON)
                        .execute().actionGet();
            }
            return true;
        } catch (ServiceException se) {
            throw se;
        } catch (Exception e) {
            throw new ServiceException(ResultCode.ERROR_UPDATE, e);
        }
    }

    @Override
    public boolean delete(T model, Index index) throws ServiceException {
        try {
            SearchResponse response = null;
            try {
                response = this.getSearchRequestBuilder(index)
                        .setScroll(this.getTimeout())
                        .setSize(1000)
                        .setQuery(this.getQuery(model, OperationType.DELETE))
                        .execute().actionGet();
            } catch (IndexNotFoundException e) {
                throw new ServiceException(ResultCode.ERROR_INDEX_MISSING);
            }

            if (response.getHits().getHits().length == 0) {
                return true;
            }
            while (true) {
                BulkRequestBuilder bulkRequestBuilder = this.getClient(index).prepareBulk();
                for (SearchHit searchHit : response.getHits().getHits()) {
                    bulkRequestBuilder.add(this.getClient(index).prepareDelete(searchHit.getIndex(), searchHit.getType(), searchHit.getId())/*.setRefresh(true)*/);
                }
                if (bulkRequestBuilder.numberOfActions() > 0) {
                    BulkResponse responses = bulkRequestBuilder.execute().actionGet();
                    if (responses.hasFailures()) {
                        throw new ServiceException(ResultCode.ERROR_DELETE);
                    }
                }
                response = this.getClient(index).prepareSearchScroll(response.getScrollId()).setScroll(getTimeout()).execute().actionGet();
                //Break condition: No hits are returned
                if (response.getHits().getHits().length == 0) {
                    return true;
                }
            }
        } catch (ServiceException se) {
            throw se;
        } catch (Exception e) {
            throw new ServiceException(ResultCode.ERROR_DELETE, e);
        }

    }

    @Override
    public boolean deletes(List<T> model, Index index) throws ServiceException {
        for (T m : model) {
            this.delete(m, index);
        }
        return true;
    }

    private SearchResponse get(T model, Index index, OperationType type) throws ServiceException {
        SearchRequestBuilder builder = this.getSearchRequestBuilder(index)
                .setQuery(this.getQuery(model, type)).setSize(1);
        SortBuilder sortBuilder = this.getSort(model, type);
        if (sortBuilder != null) {
            builder.addSort(sortBuilder);
        }
        SearchResponse response = null;
        try {
            response = builder.execute().actionGet();
        } catch (IndexNotFoundException e) {
            throw new ServiceException(ResultCode.ERROR_INDEX_MISSING);
        }
        return response;
    }

    @Override
    public T get(T model, Index index) throws ServiceException {
        SearchResponse response = this.get(model, index, OperationType.GET);
        if (response.getHits().getTotalHits() >= 1) {
            return (T) JSON.parseObject(response.getHits().getAt(0).getSourceAsString(), model.getClass());
        }
        return null;
    }

    @Override
    public Page<T> page(PageParam pageParam, T model, Index index) throws ServiceException {

        SearchRequestBuilder searchRequestBuilder = this.getSearchRequestBuilder(index);
        QueryBuilder query = this.getQuery(model, OperationType.LIST);
        if (query != null) {
            searchRequestBuilder.setQuery(query);
        }
        SortBuilder sortBuilder = this.getSort(model, OperationType.LIST);
        if (sortBuilder != null) {
            searchRequestBuilder.addSort(sortBuilder);
        }


        Page<T> page = this.buildPage(searchRequestBuilder, pageParam);
        SearchResponse response = null;
        boolean useScroll = false;
        if (page.getPageSize() > 1000 || page.getPageSize() == -1) { //大于1000每页  和 不需要分页 均用过Scroll获取
            useScroll = true;
            response = searchRequestBuilder.setScroll(this.getTimeout()).setSize(1000).execute().actionGet();
        } else {
            response = searchRequestBuilder.setFrom(page.getStartRow()).setSize(page.getPageSize()).execute().actionGet();
        }
        if (response.getHits().getHits().length == 0) {
            return page;
        }
        page.setResults(new ArrayList());
        while (true) {
            for (SearchHit searchHit : response.getHits().getHits()) {
                page.getResults().add((T) JSON.parseObject(searchHit.getSourceAsString(), model.getClass()));
            }
            if (useScroll) {
                response = this.getClient(index).prepareSearchScroll(response.getScrollId()).setScroll(getTimeout()).execute().actionGet();
                if (response.getHits().getHits().length == 0) {
                    break;
                }
            }
            if (!useScroll) {
                break;
            }
        }

        return page;

    }

    public Page buildPage(SearchRequestBuilder request, PageParam pageParam) throws ServiceException {

        //calc total
        SearchResponse response = null;
        try {
            response = request.setSize(0).execute().actionGet();
        } catch (IndexNotFoundException e) {
            throw new ServiceException(ResultCode.ERROR_INDEX_MISSING);
        }
        Integer totalHits = (int) response.getHits().getTotalHits();

        PageWrapper pageWrapper = new PageWrapper();

        pageWrapper.setTotalCount(totalHits);
        pageWrapper.setPageNo(pageParam.getPageNo());
        pageWrapper.setPageSize(pageParam.getPageSize());

        if (pageParam.getPageSize() == 0) {
            pageWrapper.setPageNo(1);
            pageWrapper.setPageSize(totalHits);
            pageWrapper.setTotalPage(1);
            pageWrapper.setTotalCount(totalHits);
        }
        return pageWrapper.toPage();
    }

    public static enum OperationType {
        GET(1, "GET"),
        UPDATE(2, "UPDATE"),
        DELETE(3, "DELETE"),
        LIST(4, "LIST");

        private Integer code;
        private String desc;

        private OperationType(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return this.code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getDesc() {
            return this.desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

}
