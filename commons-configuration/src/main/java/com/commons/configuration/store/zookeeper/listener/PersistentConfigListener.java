package com.commons.configuration.store.zookeeper.listener;

import com.alibaba.fastjson.JSON;
import com.commons.common.utils.StringUtil;
import com.commons.configuration.ICommonConfiguration;
import com.commons.configuration.model.CommonConfigBundle;
import com.commons.metadata.exception.ServiceException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.utils.ZKPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;


/**
 * Copyright (C)
 * ProxyNodeCacheListener
 * Author: jameslinlu
 */
public class PersistentConfigListener implements TreeCacheListener {

    final static Logger logger = LoggerFactory.getLogger(PersistentConfigListener.class);

    private TreeCache treeCache;
    private ICommonConfiguration commonConfiguration;

    public PersistentConfigListener(TreeCache treeCache, ICommonConfiguration commonConfiguration) {
        this.treeCache = treeCache;
        this.commonConfiguration = commonConfiguration;
    }

    private String validEventData(TreeCacheEvent event) {
        ChildData data = event.getData();
        if (data == null) {
            return null;
        }
        byte[] bytes = data.getData();
        if (bytes == null) {
            return null;
        }
        String json = null;
        try {
            json = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("Fail Read Utf8 Configuration ", e);
        }
        if (StringUtil.isEmpty(json)) {
            return null;
        }
        return json;
    }

    private void deleteCacheProperties(TreeCacheEvent event) {
        try {
            String nodeValue = this.validEventData(event);
            if (nodeValue == null) {
                return;
            }
            CommonConfigBundle bundle = null;
            try {
                bundle = JSON.parseObject(nodeValue, CommonConfigBundle.class);
            } catch (Exception e) {
                logger.error("Json Parse Config Fail {}", nodeValue, e);
            }
            this.commonConfiguration.deleteLocalCache(bundle);
        } catch (ServiceException e) {
            logger.error("Refresh Cache Properties Fail", e);
        }
    }

    private void refreshCacheProperties(TreeCacheEvent event) {
        try {
            String nodeValue = this.validEventData(event);
            if (nodeValue == null) {
                return;
            }
            CommonConfigBundle bundle = null;
            try {
                bundle = JSON.parseObject(nodeValue, CommonConfigBundle.class);
            } catch (Exception e) {
                logger.error("Json Parse Config Fail {}", nodeValue, e);
            }
            this.commonConfiguration.refreshLocalCache(bundle);
        } catch (ServiceException e) {
            logger.error("Refresh Cache Properties Fail", e);
        }
    }

    @Override
    public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent event) throws Exception {

        switch (event.getType()) {
            case NODE_ADDED: {
                this.refreshCacheProperties(event);
                logger.debug("Config Node added: {}", ZKPaths.getNodeFromPath(event.getData().getPath()));
                break;
            }
            case NODE_UPDATED: {
                this.refreshCacheProperties(event);
                logger.debug("Config Node changed: {} ", ZKPaths.getNodeFromPath(event.getData().getPath()));
                break;
            }
            case NODE_REMOVED: {
                logger.debug("Config Node removed: {}", ZKPaths.getNodeFromPath(event.getData().getPath()));
                break;
            }
            case CONNECTION_SUSPENDED: {
                logger.debug("Config CONNECTION_SUSPENDED");
                break;
                //暂停 web打断点超时触发
            }
            case CONNECTION_RECONNECTED: {
                logger.debug("Config CONNECTION_RECONNECTED");
                try {
                    treeCache.start();
                } catch (Exception e) {
                    logger.info("ReConnected Error", e);
                }
                break;
            }
            case CONNECTION_LOST: {
                logger.debug("Config CONNECTION_LOST");
                break;
            }
            case INITIALIZED: {
                logger.debug("Config INITIALIZED");
                break;
            }

        }
    }


}
