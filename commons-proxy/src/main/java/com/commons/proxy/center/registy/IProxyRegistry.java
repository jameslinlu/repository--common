package com.commons.proxy.center.registy;

import com.commons.metadata.exception.ServiceException;
import com.commons.proxy.center.config.BaseProxyConfig;
import com.commons.proxy.center.model.ServiceInfo;

import java.util.List;

/**
 * 代理注册接口
 * 提供发布和订阅
 * Copyright (C)
 * IZookeeperProxyConfig
 * Author: jameslinlu
 */
public interface IProxyRegistry {

    /**
     * 服务注册
     */
    void register(List<ServiceInfo> services) throws ServiceException;

    BaseProxyConfig getProxyConfig();

}
