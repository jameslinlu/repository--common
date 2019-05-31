package com.commons.proxy.server.utils;

import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.support.RemoteInvocationUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CancellationException;

/**
 * Copyright (C)
 * ServiceExporterUtil
 * Author: jameslinlu
 */
public class ServiceExporterUtil {

    private static Logger logger = LoggerFactory.getLogger(ServiceExporterUtil.class);

    /**
     * 判断异常并包装
     *
     * @param exception
     * @return
     */
    public static ServiceException wrapperException(Throwable exception) {
        if (exception != null) {
            logger.error("Exporter Inner Throw Error  ", exception);
            Throwable exToThrow = exception;
            if (exception instanceof InvocationTargetException) {
                exToThrow = ((InvocationTargetException) exception).getTargetException();
            }
            RemoteInvocationUtils.fillInClientStackTraceIfPossible(exToThrow);
            if (exToThrow instanceof CancellationException) {
                return new ServiceException(ResultCode.OPERATE_TIMEOUT, exToThrow);
            }
            if (exToThrow instanceof ServiceException) {
                return new ServiceException(exToThrow);
            }
            return new ServiceException(ResultCode.ERROR_INNER);
        }
        return null;
    }
}
