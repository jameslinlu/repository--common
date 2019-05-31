package com.commons.configuration.store;

import com.commons.configuration.ICommonConfiguration;
import com.commons.configuration.model.CommonConfigBundle;
import com.commons.metadata.exception.ServiceException;

/**
 * 配置存储接口
 * Copyright (C)
 * ICommonConfigStore
 * Author: jameslinlu
 */
public interface ICommonConfigStore {

    /**
     * 用于持久化配置内容过期后删除
     *
     * @param configBundle
     * @throws ServiceException
     */
    void delete(CommonConfigBundle configBundle) throws ServiceException;

    /**
     * 发布配置   新增或更新
     * 定期更新本地配置及持久化配置时 使用
     *
     * @param configBundle
     * @throws ServiceException
     */
    void publish(CommonConfigBundle configBundle) throws ServiceException;

    /**
     * 按集群获取持久化配置
     * 暂未支持读取私有配置,但可下发
     *
     * @param domain
     * @return
     * @throws ServiceException
     */
    CommonConfigBundle get(String domain) throws ServiceException;

    /**
     * 监听变更
     *
     * @throws ServiceException
     */
    void listen(ICommonConfiguration commonConfiguration) throws ServiceException;

}
