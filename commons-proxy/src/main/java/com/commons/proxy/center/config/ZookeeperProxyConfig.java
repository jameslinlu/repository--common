package com.commons.proxy.center.config;

import com.commons.common.support.spring.zookeeper.ZookeeperManager;
import org.apache.curator.framework.CuratorFramework;

/**
 * 注入zkClient调用者发布和接受数据
 * Copyright (C)
 * ZkConfigManager
 * Author: jameslinlu
 */
public class ZookeeperProxyConfig extends BaseProxyConfig {


    private String rootPath = "/zyuc/commons/proxy";
    private String providePath = "/providers";
    private String managerKey;
    private ZookeeperManager zookeeperManager;
    private CuratorFramework client;

    /**
     * 获取提供方ZK路径
     */
    public String getProvidePath() {
        return getRootPath() + this.providePath;
    }

    public void setProvidePath(String providePath) {
        this.providePath = providePath;
    }

    public CuratorFramework getClient() {
        if (client == null) {
            client = zookeeperManager.get(managerKey);
        }
        return client;
    }

    public ZookeeperManager getZookeeperManager() {
        return zookeeperManager;
    }

    public void setClient(CuratorFramework client) {
        this.client = client;
    }

    public void setZookeeperManager(ZookeeperManager zookeeperManager) {
        this.zookeeperManager = zookeeperManager;
    }

    public String getManagerKey() {
        return managerKey;
    }

    public void setManagerKey(String managerKey) {
        this.managerKey = managerKey;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
}
