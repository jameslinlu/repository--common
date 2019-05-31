package com.commons.proxy.center.discovery;

import com.commons.metadata.exception.ServiceException;
import com.commons.proxy.center.config.BaseProxyConfig;
import com.commons.proxy.center.route.IProxyRoute;

/**
 * 服务发现接口
 * Copyright (C)
 * IProxyDiscovery
 * Author: jameslinlu
 */
public interface IProxyDiscovery {

    /**
     * 基础配置
     *
     * @param config
     */
    void setProxyConfig(BaseProxyConfig config);

    /**
     * @param proxyRoute
     */
    void setProxyRoute(IProxyRoute proxyRoute);

    /**
     * 监听服务 更新 IProxyRoute
     *
     * @throws ServiceException
     */
    void discovery() throws ServiceException;

    void destroy() throws ServiceException;
}
