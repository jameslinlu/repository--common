package com.commons.dfs.client;

import com.commons.dfs.config.CommonDfsConfig;
import com.commons.dfs.operations.IDfsOperation;
import com.commons.metadata.exception.ServiceException;


/**
 * Copyright (C)
 * IDFSClient
 * Author: jameslinlu
 */
public interface IDfsClient {

    /**
     * 初始化
     * 根据配置类型调用工厂初始化实现接口
     *
     * @param config
     */
    void initialize(CommonDfsConfig config) throws ServiceException;

    /**
     * 获取dfs 操作接口
     *
     * @return
     */
    IDfsOperation getOpts() throws ServiceException;

    /**
     * 销毁实例
     */
    void destroy() throws ServiceException;
}
