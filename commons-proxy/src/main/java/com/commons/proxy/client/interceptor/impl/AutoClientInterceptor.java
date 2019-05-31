package com.commons.proxy.client.interceptor.impl;

import com.commons.proxy.center.model.ProxyConfigure;
import com.commons.proxy.center.provide.IProxyProvider;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自动适配客户端代理
 * 默认non-block
 * Copyright (C)
 * AbstractCommonClientInterceptor
 * Author: jameslinlu
 */
public class AutoClientInterceptor extends CommonClientInterceptor {

    private static Logger logger = LoggerFactory.getLogger(AutoClientInterceptor.class);

    public AutoClientInterceptor(ProxyConfigure configure, IProxyProvider provider) {
        super(configure, provider);
    }

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        logger.debug("Proxy Invoke");
        return super.invoke(invocation);
    }

}
