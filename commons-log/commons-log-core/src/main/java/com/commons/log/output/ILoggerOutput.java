package com.commons.log.output;

import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.log.AbstractLog;

/**
 * Copyright (C)
 * ILoggerStore
 * Author: jameslinlu
 */
public interface ILoggerOutput {

    /**
     * 处理输出日志
     *
     * @param obj
     * @throws ServiceException
     */
    void out(AbstractLog obj) throws ServiceException;

}
