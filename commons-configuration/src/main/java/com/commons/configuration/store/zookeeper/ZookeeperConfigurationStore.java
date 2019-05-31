package com.commons.configuration.store.zookeeper;

import com.alibaba.fastjson.JSON;
import com.commons.common.support.spring.zookeeper.ZookeeperManager;
import com.commons.configuration.ICommonConfiguration;
import com.commons.configuration.constant.ConfigConst;
import com.commons.configuration.model.CommonConfigBundle;
import com.commons.configuration.store.ICommonConfigStore;
import com.commons.configuration.store.zookeeper.listener.PersistentConfigListener;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import com.google.common.base.Joiner;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 持久化存储实现 Spring初始化时设置zookeeper管理器
 * Copyright (C)
 * ZookeeperConfigurationStore
 * Author: jameslinlu
 */
public class ZookeeperConfigurationStore implements ICommonConfigStore {

    final static Logger logger = LoggerFactory.getLogger(ZookeeperConfigurationStore.class);

    private final static Joiner joiner = Joiner.on("/").skipNulls();

    @Autowired
    private ZookeeperManager zookeeperManager;
    private String zookeeperKey;
    private TreeCache cache = null;
    private TreeCacheListener listener = null;

    public void setZookeeperKey(String zookeeperKey) {
        this.zookeeperKey = zookeeperKey;
    }

    private CuratorFramework getClient() {
        return zookeeperManager.get(zookeeperKey);
    }

    @Override
    public void listen(ICommonConfiguration commonConfiguration) throws ServiceException {
        try {
            //获取管理客户端
            CuratorFramework client = getClient();
            cache = new TreeCache(client, ConfigConst.Zookeeper.CONFIG_PREFIX_PATH);
            listener = new PersistentConfigListener(cache, commonConfiguration);
            cache.getListenable().addListener(listener);
            cache.start();
        } catch (Exception e) {
            logger.error("Zookeeper Config Listen Start Fail", e);
            cleanListen();
            throw new ServiceException(e);
        }
    }

    public void cleanListen() throws ServiceException {
        if (cache != null) {
            CloseableUtils.closeQuietly(cache);
        }
    }

    public void save(CommonConfigBundle configBundle) throws ServiceException {
        try {
            CreateMode mode = CreateMode.EPHEMERAL;
            //发布至ZK的均为持久化配置数据
            String nodePath = joiner.join(ConfigConst.Zookeeper.CONFIG_PREFIX_PATH, configBundle.getDomain(), configBundle.getIp(), configBundle.getPort(), configBundle.getGroup());
            String nodeValue = JSON.toJSONString(configBundle);
            this.getClient().create().creatingParentsIfNeeded()
                    .withMode(mode).withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                    .forPath(nodePath, nodeValue.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new ServiceException(ResultCode.ERROR_SAVE, e);
        }
    }

    public void delete(CommonConfigBundle configBundle) throws ServiceException {
        try {
            //用于持久化配置内容过期后删除
            String nodePath = joiner.join(ConfigConst.Zookeeper.CONFIG_PREFIX_PATH, configBundle.getDomain(), configBundle.getIp(), configBundle.getPort(), configBundle.getGroup());
            this.getClient().delete().guaranteed().forPath(nodePath);
        } catch (Exception e) {
            throw new ServiceException(ResultCode.ERROR_DELETE, e);
        }
    }

    public void publish(CommonConfigBundle configBundle) throws ServiceException {
        try {
            String nodePath = joiner.join(ConfigConst.Zookeeper.CONFIG_PREFIX_PATH, configBundle.getDomain(), configBundle.getIp(), configBundle.getPort(), configBundle.getGroup());
            String nodeValue = JSON.toJSONString(configBundle);
            boolean exist = this.getClient().checkExists().forPath(nodePath) != null;
            if (exist) {
                //update
                this.getClient().setData().forPath(nodePath, nodeValue.getBytes("UTF-8"));
            } else {
                this.save(configBundle);
            }
        } catch (Exception e) {
            throw new ServiceException(ResultCode.ERROR_UPDATE, e);
        }
    }

    public CommonConfigBundle get(String domain) throws ServiceException {
        try {
            String nodePath = joiner.join(ConfigConst.Zookeeper.CONFIG_PREFIX_PATH, domain);
            List<String> nodePaths = this.getClient().getChildren().forPath(nodePath);
            if (nodePaths == null || nodePaths.isEmpty()) {
                return null;
            }
            for (String path : nodePaths) {
                System.out.println(path);
            }
            return null;
        } catch (Exception e) {
            throw new ServiceException(ResultCode.ERROR_SEARCH, e);
        }
    }
}
