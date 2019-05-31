package com.commons.configuration;

import com.commons.configuration.model.CommonConfigBundle;
import com.commons.configuration.support.ICommonConfigurationExtract;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.MachineConfig;

import java.util.List;

/**
 * Copyright (C)
 * ICommonConfiguration
 * Author: jameslinlu
 */
public interface ICommonConfiguration {

    /**
     * 设置配置扩展
     *
     * @param extract
     * @throws ServiceException
     */
    void setExtract(ICommonConfigurationExtract extract) throws ServiceException;

    /**
     * 设置机器配置
     *
     * @param config
     */
    void setMachineConfig(MachineConfig config);

    /**
     * 注册配置
     * CommonConfigBundle 含ip和端口的为私有配置 只有domain的为全局配置
     *
     * @throws ServiceException
     */
    void register(List<CommonConfigBundle> bundles) throws ServiceException;

    /**
     * 注册默认抓取配置
     *
     * @throws ServiceException
     */
    void extract() throws ServiceException;

    /**
     * 监听持久配置变更
     *
     * @throws ServiceException
     */
    void listen() throws ServiceException;

    /**
     * 读取持久化配置
     * 通过对实现类配置的domain获取集群内持久化配置
     *
     * @return
     * @throws ServiceException
     */
    List<CommonConfigBundle> getPersistentConfigs() throws ServiceException;

    /**
     * 刷新本地缓存
     * 将监听数据的变化后调用
     *
     * @throws ServiceException
     */
    void refreshLocalCache(CommonConfigBundle bundle) throws ServiceException;

    /**
     * 删除本地缓存
     * 将监听数据的变化后调用
     *
     * @param bundle
     * @throws ServiceException
     */
    void deleteLocalCache(CommonConfigBundle bundle) throws ServiceException;
}
