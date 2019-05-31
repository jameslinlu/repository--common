package com.commons.log.filter;


import com.commons.metadata.model.log.AbstractLog;

/**
 * 日志前置和后置扩展处理
 * Copyright (C)
 * ILoggerFilter
 * Author: jameslinlu
 */
public interface ILoggerFilter {

    /**
     * 前置过滤
     */
    void before(AbstractLog log);

    /**
     * 后置过滤
     */
    void after(AbstractLog log);
}
