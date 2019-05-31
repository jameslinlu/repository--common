package com.commons.dfs;

import com.commons.dfs.client.IDfsClient;
import com.commons.dfs.client.impl.FastDfsClient;
import com.commons.dfs.config.CommonDfsConfig;
import com.commons.dfs.config.FastDfsConfig;
import com.commons.metadata.exception.ServiceException;

/**
 * Copyright (C)
 * DfsClientFactory
 * Author: jameslinlu
 */
public class DfsClientFactory {

    /**
     * 通过配置文件构造实例
     *
     * @param config
     * @return
     */
    public static IDfsClient create(CommonDfsConfig config) {
        IDfsClient client = null;
        if (config instanceof FastDfsConfig) {
            try {
                client = new FastDfsClient(config);
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }
        return client;
    }
}
