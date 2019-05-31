package com.commons.common.support.spring.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C)
 * ZookeeperManager
 * Author: jameslinlu
 */
public class ZookeeperManager {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperManager.class);

    Map<String, ZookeeperConfiguration> resources = new HashMap<>();
    Map<String, CuratorFramework> clients = new HashMap<>();

    /**
     * 设置key和 zk配置
     *
     * @param resources
     */
    public void setResources(Map<String, ZookeeperConfiguration> resources) {
        this.resources = resources;
    }

    /**
     * 获取client
     *
     * @param key
     * @return
     */
    public CuratorFramework get(String key) {
        return clients.get(key);
    }

    /**
     * 注册zk配置
     *
     * @param key
     * @param config
     */
    public void register(String key, ZookeeperConfiguration config) {
        CuratorFramework client = CuratorFrameworkFactory.newClient(config.getConnectString(), config.getSessionTimeoutMs(), config.getConnectionTimeoutMs(), config.getRetryPolicy());
        client.start();
        logger.debug("register zookeeper client {}", config.getConnectString());
        clients.put(key, client);
    }

    /**
     * 初始化启动
     */
    public void start() {
        CuratorFramework client = null;
        ZookeeperConfiguration config = null;
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
            for (CuratorFramework framework : clients.values()) {
                framework.close();
            }
        }
        clients = null;
    }
}
