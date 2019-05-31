package com.commons.proxy.center.provide;

import com.commons.metadata.exception.ServiceException;
import com.commons.proxy.center.model.ServiceInfo;
import com.commons.proxy.center.policy.ILoadPolicy;
import com.commons.proxy.center.route.IProxyRoute;

import java.net.URL;

/**
 * Copyright (C)
 * IProxyProvider
 * Author: jameslinlu
 */
public interface IProxyProvider {

    void setLoadPolicy(ILoadPolicy loadPolicy);

    void setProxyRoute(IProxyRoute proxyRoute);

    ILoadPolicy getLoadPolicy();

    IProxyRoute getProxyRoute();

    ServiceInfo getProxyService(String interfaceName, float maxVersion, String proxyType) throws ServiceException;

    URL getProxyServiceURL(String interfaceName, float maxVersion, String proxyType) throws ServiceException;

    ServiceInfo getProxyService(String interfaceName, float maxVersion) throws ServiceException;

    URL getProxyServiceURL(String interfaceName, float maxVersion) throws ServiceException;

}
