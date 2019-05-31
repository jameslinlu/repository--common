package com.commons.mq.client;

import com.commons.mq.connect.config.AbstractMQConfig;
import com.commons.mq.connect.factory.CommonMQClientFactory;
import org.springframework.context.support.ApplicationObjectSupport;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C)
 * MQClientManager
 * Author: jameslinlu
 */
public class MQClientManager extends ApplicationObjectSupport {


    Map<String, AbstractMQConfig> resources = new HashMap<>();
    Map<String, IMQClient> clients = new HashMap<>();

    /**
     * 设置key和 mq配置
     *
     * @param resources
     */
    public void setResources(Map<String, AbstractMQConfig> resources) {
        this.resources = resources;
    }

    /**
     * 获取client
     *
     * @param key
     * @return
     */
    public IMQClient get(String key) {
        return clients.get(key);
    }

    /**
     * 注册mq配置
     *
     * @param key
     * @param config
     */
    public void register(String key, AbstractMQConfig config) {
        IMQClient client = CommonMQClientFactory.newClient(config);
        client.initialize(this.getApplicationContext());
        clients.put(key, client);
    }

    /**
     * 初始化启动
     */
    public void start() {
        IMQClient client = null;
        AbstractMQConfig config = null;
        for (String key : resources.keySet()) {
            config = resources.get(key);
            register(key, config);
        }
    }

    /**
     * 关闭销毁
     */
    public void close() {
        if (clients != null) {
            for (IMQClient framework : clients.values()) {
                framework.destroy();
            }
        }
        clients = null;
    }

}
