package com.commons.log.convert;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.commons.log.utils.LoggerContextUtil;

/**
 * 用于logback 日志输出 traceId
 * Created by guqw on 2017/11/30.
 */
public class LoggerTraceIdConverter extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent iLoggingEvent) {
        return LoggerContextUtil.getTraceId();
    }
}
