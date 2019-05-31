package com.commons.proxy.client.interceptor.impl;

import com.commons.metadata.exception.ServiceException;
import com.commons.proxy.center.model.ProxyConfigure;
import com.commons.proxy.center.model.ServiceInfo;
import com.commons.proxy.center.provide.IProxyProvider;
import com.commons.proxy.center.transfer.factory.ProxyTransferFactory;
import com.commons.proxy.center.transfer.model.TransferRequestMessage;
import com.commons.proxy.client.core.impl.CommonProxy;
import com.commons.proxy.client.interceptor.ICommonClientInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.util.concurrent.ListenableFutureTask;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * Copyright (C)
 * AbstractCommonClientInterceptor
 * Author: jameslinlu
 */
public abstract class CommonClientInterceptor extends CommonProxy implements ICommonClientInterceptor {

    private static Logger logger = LoggerFactory.getLogger(CommonClientInterceptor.class);

    public CommonClientInterceptor(ProxyConfigure configure, IProxyProvider provider) {
        super(configure, provider);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        /**
         * invoke 方法只可以抛出经过检查的异常类型，该异常类型可以使用 代理接口（可以通过它调用）中方法的 throws 子句指派一种异常类型。
         * 如果 invoke 方法抛出一个经过检查的异常，该异常没有指派给任何由一个代理接口（可以通过它调用）中的方法声明的异常类型，
         * 那么该代理实例上的调用将抛出一个未经检查的 UndeclaredThrowableException。此限制表示并非所有的由传递到 invoke 方法的 Method 对象上调用 getExceptionTypes 返回的异常类型都可以由 invoke 方法成功抛出。
         */
        Object specialObj = invokeSpecial(invocation);
        if (specialObj != null) {
            return specialObj;
        }
        final ServiceInfo service = this.getService();
        final TransferRequestMessage message = ProxyTransferFactory.buildRequestMessage(service, invocation);
        if (getExecutor() == null) {
            //无线程执行器
            return syncTransferRequest(message);
        } else {
            return asyncTransferRequest(message);
        }
    }

    public Object invokeSpecial(MethodInvocation invocation) {
        // equals and hashCode and toString are special cased
        Method method = invocation.getMethod();
        Object[] arguments = invocation.getArguments();
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (methodName.equals("equals") && parameterTypes.length == 1 && parameterTypes[0].equals(Object.class)) {
            return true;
        } else if (methodName.equals("hashCode") && arguments.length == 0) {
            return new Integer(this.hashCode());
        } else if (methodName.equals("toString") && arguments.length == 0) {
            return this.toString();
        }
        return null;
    }

    public Object asyncTransferRequest(final TransferRequestMessage message) throws Throwable {
        //子线程包装  线程阻塞get()
        Callable<Object> task = new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try {
                    return syncTransferRequest(message);
                } catch (Throwable ex) {
                    return new ServiceException(ex);
                }
            }
        };
        ListenableFutureTask<Object> future = new ListenableFutureTask(task);
        ((AsyncListenableTaskExecutor) getExecutor()).submitListenable(future);
        Object obj = future.get();//阻塞是为了让服务器确认消费 规避连不上服务器情况 服务器标示为异步则立即返回,服务器为NIO处理
        if (obj instanceof Exception) {
            throw new ServiceException((Exception) obj);
        }
        return obj;

    }


}
