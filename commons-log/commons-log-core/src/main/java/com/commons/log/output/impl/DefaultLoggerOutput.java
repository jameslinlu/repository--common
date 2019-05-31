package com.commons.log.output.impl;

import com.alibaba.fastjson.JSON;
import com.commons.log.output.ILoggerOutput;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.log.AbstractLog;
import com.commons.metadata.model.log.enums.LoggerLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 默认的日志记录处理类
 * Copyright (C)
 * LogExecutor
 * Author: jameslinlu
 */
public class DefaultLoggerOutput implements ILoggerOutput {

    private static final Logger logger = LoggerFactory.getLogger(DefaultLoggerOutput.class);

    @Override
    public void out(AbstractLog obj) throws ServiceException {
        obj.setTime(new Date());
        print(obj);
    }

    public void print(AbstractLog log) throws ServiceException {
        String level = log.getLevel();
        if (level == null) {
            return;
        }
        LoggerLevel loggerLevel = LoggerLevel.getEnum(level);
        if (loggerLevel == null) {
            return;
        }
        switch (loggerLevel) {
            case TRACE:
                logger.trace(JSON.toJSONString(log));
                break;
            case DEBUG:
                logger.debug(JSON.toJSONString(log));
                break;
            case INFO:
                logger.info(JSON.toJSONString(log));
                break;
            case WARN:
                logger.warn(JSON.toJSONString(log));
                break;
            case ERROR:
                logger.error(JSON.toJSONString(log));
                break;
        }
    }


}
