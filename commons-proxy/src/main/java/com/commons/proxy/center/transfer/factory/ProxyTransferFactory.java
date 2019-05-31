package com.commons.proxy.center.transfer.factory;

import com.commons.common.utils.Identities;
import com.commons.common.utils.SecurityUtils;
import com.commons.log.utils.LoggerContextUtil;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import com.commons.proxy.center.model.ProxyConfigure;
import com.commons.proxy.center.model.ServiceInfo;
import com.commons.proxy.center.secure.RequestAuthorizeUtil;
import com.commons.proxy.center.transfer.IProxyTransfer;
import com.commons.proxy.center.transfer.impl.HttpProxyTransfer;
import com.commons.proxy.center.transfer.impl.MQProxyTransfer;
import com.commons.proxy.center.transfer.model.TransferRequestMessage;
import com.commons.proxy.center.transfer.model.TransferType;
import com.commons.proxy.serializable.SerializerType;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Copyright (C)
 * IProxyTransfer
 * Author: jameslinlu
 */
public class ProxyTransferFactory {

    private static Logger logger = LoggerFactory.getLogger(ProxyTransferFactory.class);

    public static IProxyTransfer create(TransferType transferType, ProxyConfigure config) throws ServiceException {

        switch (transferType) {
            case HTTP:
                return HttpProxyTransfer.getInstance();
            case NETTY:
                return null;
            case MQ:
                return MQProxyTransfer.getInstance(config);
            default:
                logger.error("ProxyTransferFactory Transfer Enums Not Include {}", transferType);
                throw new ServiceException(ResultCode.ERROR_PARAMETER);
        }

    }

    public static TransferRequestMessage buildRequestMessage(ServiceInfo serviceInfo, MethodInvocation invocation) {

        Method method = invocation.getMethod();
        Object[] arguments = invocation.getArguments();
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        TransferType transferType = TransferType.getEnum(serviceInfo.getTransfer());
        SerializerType serializerType = SerializerType.getEnum(serviceInfo.getSerializer());
        boolean isAsync = transferType == TransferType.MQ;
        String messageId = Identities.getRandomGUID(true);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String prefix = RequestAuthorizeUtil.extractSignPrefix(messageId, timestamp);
        String signature = SecurityUtils.md5(prefix + timestamp).toUpperCase();
        String cluster = serviceInfo.getPlatform().getDomain();
        float timeout = serviceInfo.getTimeout();
        float delay = serviceInfo.getDelay();

        TransferRequestMessage message = new TransferRequestMessage();
        message.setCluster(cluster);
        message.setTimeout(timeout);
        message.setDelay(delay);
        message.setUrl(serviceInfo.getPlatform().getUrl() + serviceInfo.getName());
        message.setAsync(isAsync);
        message.setInterfaze(serviceInfo.getInterfaceName());
        message.setMethod(methodName);
        message.setParameters(arguments);
        message.setParameterTypes(parameterTypes);
        message.setSerializerType(serializerType.toString());
        message.setTransferType(transferType.toString());
        message.setVersion(Float.valueOf(serviceInfo.getVersion()));
        message.setMessageId(messageId);
        message.setTimestamp(timestamp);
        message.setSignature(signature);
        message.setTraceId(LoggerContextUtil.getTraceId());
        message.setSequenceId(LoggerContextUtil.getSequenceId() + LoggerContextUtil.getSequenceIdLocalAndIncrement());

        return message;
    }

}
