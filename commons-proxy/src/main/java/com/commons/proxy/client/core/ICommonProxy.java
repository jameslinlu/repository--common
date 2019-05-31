package com.commons.proxy.client.core;

import com.commons.metadata.exception.ServiceException;
import com.commons.proxy.center.model.ProxyConfigure;
import com.commons.proxy.center.model.ServiceInfo;
import com.commons.proxy.center.policy.ILoadPolicy;
import com.commons.proxy.center.provide.IProxyProvider;
import com.commons.proxy.center.route.IProxyRoute;
import com.commons.proxy.center.transfer.IProxyTransfer;
import com.commons.proxy.center.transfer.model.TransferRequestMessage;
import com.commons.proxy.center.transfer.model.TransferType;
import org.springframework.core.task.AsyncTaskExecutor;

/**
 * Copyright (C)
 * AbstractCommonProxy
 * Author: jameslinlu
 */
public interface ICommonProxy {

    ProxyConfigure getConfigure();

    AsyncTaskExecutor getExecutor();

    IProxyRoute getRouter();

    ILoadPolicy getPolicy();

    IProxyProvider getProvider();

    ServiceInfo getService() throws ServiceException;

    IProxyTransfer getTransfer(TransferType transferType, ProxyConfigure config) throws ServiceException;

    Object syncTransferRequest(TransferRequestMessage message) throws ServiceException;
}
