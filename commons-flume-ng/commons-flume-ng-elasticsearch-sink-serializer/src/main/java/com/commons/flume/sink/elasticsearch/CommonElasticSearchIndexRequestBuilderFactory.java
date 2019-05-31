package com.commons.flume.sink.elasticsearch;

import com.alibaba.fastjson.JSON;
import com.commons.flume.sink.elasticsearch.utils.JedisHelper;
import com.commons.flume.sink.elasticsearch.utils.Reflections;
import com.commons.metadata.model.collect.IParse;
import com.google.common.base.Throwables;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.conf.ComponentConfiguration;
import org.apache.flume.sink.elasticsearch.ElasticSearchIndexRequestBuilderFactory;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * 自定义Sink解析 并通过IParse接口构造elasticsearch请求
 * Copyright (C)
 * ChargingElasticSearchIndexRequestBuilderFactory
 * Author: jameslinlu
 */
public class CommonElasticSearchIndexRequestBuilderFactory implements ElasticSearchIndexRequestBuilderFactory {

    private static final Logger logger = LoggerFactory.getLogger(CommonElasticSearchIndexRequestBuilderFactory.class);

    //格式化后缀 yyyyMMdd
    private String formatPattern;
    private String dateFieldName;
    private IParse parse;


    @Override
    public IndexRequestBuilder createIndexRequest(Client client, String indexPrefix, String indexType, Event event) throws IOException {
        Object obj = parse.parseEvent(event);
        if (obj == null) {
            return null;
        }
        String json = JSON.toJSONString(obj);
        logger.debug("Parse text to json:{}", json);
        String index = indexPrefix;
        if (formatPattern != null && !formatPattern.isEmpty()) {
            if (dateFieldName != null && !dateFieldName.isEmpty()) {
                Object dataFieldObj = null;
                if (obj instanceof Map) {
                    dataFieldObj = ((Map) obj).get(dateFieldName);
                } else {
                    dataFieldObj = Reflections.getFieldValue(obj, dateFieldName);
                }
                Date date = dataFieldObj instanceof Date ? (Date) dataFieldObj : (dataFieldObj instanceof Long ? new Date((Long) dataFieldObj) : new Date());
                index += DateFormatUtils.format(date, formatPattern);

            } else {
                index += DateFormatUtils.format(new Date(), formatPattern);
            }
        }
        logger.debug("Create index request {}:{} create", index, indexType);
        //setCreate 方法 在5.0不会自动生成id  无法插入
        return client.prepareIndex(index, indexType).setOpType(DocWriteRequest.OpType.INDEX).setSource(json, XContentType.JSON);
    }

    @Override
    public void configure(Context context) {
        dateFieldName = context.getString("dateFieldName", null);
        formatPattern = context.getString("formatPattern", null);
        if (parse == null) {
            String parseClazz = context.getString("parse");
            logger.debug("configure parse class {}", parseClazz);
            try {
                Class<? extends IParse> clazz = (Class<? extends IParse>) Class.forName(parseClazz);
                parse = clazz.newInstance();
                parse.configure(context);
            } catch (ClassNotFoundException e) {
                logger.error("parse class not found error ", e);
                Throwables.propagate(e);
            } catch (InstantiationException e) {
                logger.error("new instance error ", e);
                Throwables.propagate(e);
            } catch (IllegalAccessException e) {
                logger.error("class illegal access error ", e);
                Throwables.propagate(e);
            }
        }
        String redisKey = context.getString(JedisHelper.REDIS_KEY);
        if (redisKey != null && !redisKey.equals("")) {
            logger.debug("configure redis key {}", redisKey);
            JedisHelper.getInstance(redisKey).configure(context);
        }
    }

    @Override
    public void configure(ComponentConfiguration componentConfiguration) {

    }

}
