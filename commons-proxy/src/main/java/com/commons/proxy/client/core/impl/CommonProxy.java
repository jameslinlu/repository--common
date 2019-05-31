package com.commons.proxy.client.core.impl;

import com.commons.common.utils.ContextUtil;
import com.commons.metadata.exception.ServiceException;
import com.commons.proxy.center.model.ProxyConfigure;
import com.commons.proxy.center.model.ServiceInfo;
import com.commons.proxy.center.policy.ILoadPolicy;
import com.commons.proxy.center.provide.IProxyProvider;
import com.commons.proxy.center.route.IProxyRoute;
import com.commons.proxy.center.transfer.IProxyTransfer;
import com.commons.proxy.center.transfer.factory.ProxyTransferFactory;
import com.commons.proxy.center.transfer.model.TransferRequestMessage;
import com.commons.proxy.center.transfer.model.TransferType;
import com.commons.proxy.client.core.ICommonProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;

/**
 * Copyright (C)
 * CommonProxy
 * Author: jameslinlu
 */
public class CommonProxy implements ICommonProxy {

    private static final Logger logger = LoggerFactory.getLogger(CommonProxy.class);

    public ProxyConfigure configure;
    public IProxyProvider provider;
    private AsyncTaskExecutor executor;
    private String executorName = "taskExecutor";

    public CommonProxy(ProxyConfigure configure, IProxyProvider provider) {
        this.configure = configure;
        this.provider = provider;
    }


    @Override
    public ProxyConfigure getConfigure() {
        return configure;
    }

    @Override
    public IProxyRoute getRouter() {
        return provider.getProxyRoute();
    }

    @Override
    public ILoadPolicy getPolicy() {
        return provider.getLoadPolicy();
    }

    @Override
    public IProxyProvider getProvider() {
        return provider;
    }

    @Override
    public ServiceInfo getService() throws ServiceException {
        return this.getProvider().getProxyService(this.getConfigure().getServiceInterfaceName(), this.getConfigure().getMaxVersion());
    }

    @Override
    public AsyncTaskExecutor getExecutor() {
        if (this.executor != null) {
            return this.executor;
        } else if (this.executorName != null) {
            try {
                return ContextUtil.getBean(this.executorName, AsyncTaskExecutor.class);
            } catch (Exception e) {
                logger.error("CommonProxy Context getBean Fail", e);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public Object syncTransferRequest(TransferRequestMessage message) throws ServiceException {
        IProxyTransfer transfer = getTransfer(TransferType.getEnum(message.getTransferType()), this.getConfigure());
        return transfer.send(message).getResult();
    }

    @Override
    public IProxyTransfer getTransfer(TransferType transferType, ProxyConfigure config) throws ServiceException {
        return ProxyTransferFactory.create(transferType, config);
    }

}
