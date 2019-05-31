package com.commons.index.es;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C)
 * EsManager
 * Author: jameslinlu
 */
public class ElasticSearchManager {
    Map<String, ElasticSearchConfiguration> resources = new HashMap<>();
    Map<String, Client> clients = new HashMap<>();

    /**
     * 设置key和 es配置
     *
     * @param resources
     */
    public void setResources(Map<String, ElasticSearchConfiguration> resources) {
        this.resources = resources;
    }

    /**
     * 获取client
     *
     * @param key
     * @return
     */
    public Client get(String key) {
        return clients.get(key);
    }

    /**
     * 注册zk配置
     *
     * @param key
     * @param config
     */
    public void register(String key, ElasticSearchConfiguration config) {
        Settings.Builder settingBuilder = Settings.builder();
        settingBuilder.put(config.getParams());
//        TransportClient.Builder clientBuilder = TransportClient.builder();
//        clientBuilder.settings(settingBuilder);
//        Client client = clientBuilder.build();
        Client client = new PreBuiltTransportClient(settingBuilder.build());
        ((TransportClient) client).addTransportAddress(config.getTransportAddress());
        clients.put(key, client);
    }

    /**
     * 初始化启动
     */
    public void start() {
        ElasticSearchConfiguration config = null;
        for (String key : resources.keySet()) {
            config = resources.get(key);
            register(key, config);
        }
    }

    /**
     * 关闭销毁
     */
    public void close() {
        clients = null;
    }

    public boolean delete(String key, String needDeletedIndex) {
        return ElasticSearchCleanUp.deleteIndex(get(key), needDeletedIndex);
    }

    public boolean delete(String key, String indexName, String typeName, String dateField, Date overdueDate) {
        return ElasticSearchCleanUp.deleteIndex(get(key), indexName, typeName, dateField, overdueDate);
    }
}
