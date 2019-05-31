package com.commons.proxy.server.exporter.factory;

import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import com.commons.proxy.center.transfer.model.TransferType;
import com.commons.proxy.server.handler.HttpServiceExporter;
import com.commons.proxy.server.handler.MQServiceExporter;
import com.commons.proxy.server.handler.SecurityHessianServiceExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright (C)
 * IProxyTransfer
 * Author: jameslinlu
 */
public class ExporterFactory {

    private static Logger logger = LoggerFactory.getLogger(ExporterFactory.class);

    public static Class buildClazz(TransferType transferType) throws ServiceException {
        switch (transferType) {
            case HTTP:
                return HttpServiceExporter.class;
            case HESSIAN:
                return SecurityHessianServiceExporter.class;
            case NETTY:
                return null;
            case MQ:
                return MQServiceExporter.class;
            default:
                logger.error("ProxyTransferFactory Transfer Enums Not Include {}", transferType);
                throw new ServiceException(ResultCode.ERROR_PARAMETER);
        }
    }

}
