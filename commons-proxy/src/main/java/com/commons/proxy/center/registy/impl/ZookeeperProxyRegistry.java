package com.commons.proxy.center.registy.impl;

import com.alibaba.fastjson.JSON;
import com.commons.metadata.exception.ServiceException;
import com.commons.proxy.center.config.BaseProxyConfig;
import com.commons.proxy.center.config.ZookeeperProxyConfig;
import com.commons.proxy.center.discovery.listener.ProxyNodeCacheListener;
import com.commons.proxy.center.model.Platform;
import com.commons.proxy.center.model.ServiceInfo;
import com.commons.proxy.center.registy.IProxyRegistry;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * ZK实现的代理注册
 * Copyright (C)
 * ZookeeperProxyService
 * Author: jameslinlu
 */
public class ZookeeperProxyRegistry implements IProxyRegistry {

    final static Logger logger = LoggerFactory.getLogger(ZookeeperProxyRegistry.class);

    private ZookeeperProxyConfig proxyConfig;

    public void setProxyConfig(BaseProxyConfig proxyConfig) {
        this.proxyConfig = (ZookeeperProxyConfig) proxyConfig;
    }

    @Override
    public ZookeeperProxyConfig getProxyConfig() {
        return proxyConfig;
    }

    /**
     * 注册服务
     */
    @Override
    public void register(List<ServiceInfo> services) throws ServiceException {
        try {
            Platform platform = proxyConfig.getPlatformInfo();
            platform.setServices(services);
            String nodePath = proxyConfig.getProvidePath() + "/" + platform.getIdentity();
            String nodeVal = JSON.toJSONString(platform);
            CuratorFramework client = proxyConfig.getClient();
            client.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL).withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                    .forPath(nodePath, nodeVal.getBytes());
            ProxyNodeCacheListener.lostRecoveryMap.put(nodePath, nodeVal);
            logger.debug("register proxy zk path {}", nodePath);

        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

}
