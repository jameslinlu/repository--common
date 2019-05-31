package com.commons.proxy.center.route;

import com.commons.metadata.exception.ServiceException;
import com.commons.proxy.center.model.ServiceInfo;

import java.util.List;

/**
 * 路由管理接口
 * Copyright (C)
 * IProxyRoute
 * Author: jameslinlu
 */
public interface IProxyRoute {

    void remove(String path) throws ServiceException;

    void addOrUpdate(String path, String content) throws ServiceException;

    List<ServiceInfo> getServices(String domain, String interfaceName, String proxyType, float maxVersion) throws ServiceException;

    List<ServiceInfo> getServices(String domain, String interfaceName, float maxVersion) throws ServiceException;
}
