package com.commons.proxy.client.core.factory;

import com.commons.proxy.center.model.ProxyConfigure;
import com.commons.proxy.center.provide.IProxyProvider;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteAccessor;

/**
 * 基础远程业务代理类
 * Copyright (C)
 * ProxyFactory
 * Author: jameslinlu
 */
public class CommonProxyFactoryBean extends RemoteAccessor implements InitializingBean, FactoryBean<Object> {

    private ProxyConfigure configure;
    private IProxyProvider provider;
    private Object proxy;

    public void setConfigure(ProxyConfigure configure) {
        this.configure = configure;
    }

    public void setProxyProvider(IProxyProvider provider) {
        this.provider = provider;
    }


    @Override
    public void afterPropertiesSet() {
        initializeProxy();
    }

    @Override
    public Object getObject() {
        return this.proxy;
    }

    @Override
    public Class<?> getObjectType() {
        return this.getServiceInterface();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * 按接口包装代理
     */
    public void initializeProxy() {
        this.proxy = new CommonProxyFactory(this.getServiceInterface(), configure, provider, this.getBeanClassLoader()).proxy();
    }

}
