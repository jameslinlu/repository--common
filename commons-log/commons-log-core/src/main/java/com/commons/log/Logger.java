package com.commons.log;

import com.commons.common.utils.Exceptions;
import com.commons.log.output.ILoggerOutput;
import com.commons.log.output.impl.DefaultLoggerOutput;
import com.commons.log.utils.LoggerContextUtil;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.log.InnerLog;
import com.commons.metadata.model.log.enums.LoggerLevel;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

/**
 * Copyright (C)
 * Logger
 * Author: jameslinlu
 */
public class Logger {

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(Logger.class);

    private static ILoggerOutput loggerStore = new DefaultLoggerOutput();

    public void setLoggerStore(ILoggerOutput loggerStore) {
        this.loggerStore = loggerStore;
    }

    public static void trace(String msg, Throwable t) {
        out(LoggerLevel.TRACE, msg, t);
    }

    public static void debug(String msg, Throwable t) {
        out(LoggerLevel.DEBUG, msg, t);
    }

    public static void info(String msg, Throwable t) {
        out(LoggerLevel.INFO, msg, t);
    }

    public static void warn(String msg, Throwable t) {
        out(LoggerLevel.WARN, msg, t);
    }

    public static void error(String msg, Throwable t) {
        out(LoggerLevel.ERROR, msg, t);
    }

    public static void trace(String format, Object... arguments) {
        out(LoggerLevel.TRACE, format, arguments);
    }

    public static void debug(String format, Object... arguments) {
        out(LoggerLevel.DEBUG, format, arguments);
    }

    public static void info(String format, Object... arguments) {
        out(LoggerLevel.INFO, format, arguments);
    }

    public static void warn(String format, Object... arguments) {
        out(LoggerLevel.WARN, format, arguments);

    }

    public static void error(String format, Object... arguments) {
        out(LoggerLevel.ERROR, format, arguments);
    }


    private static void out(LoggerLevel lv, String format, Object... arguments) {
        FormattingTuple ft = null;
        switch (lv) {
            case TRACE:
                if (logger.isTraceEnabled()) {
                    ft = MessageFormatter.format(format, arguments);
                }
                break;
            case DEBUG:
                if (logger.isDebugEnabled()) {
                    ft = MessageFormatter.format(format, arguments);
                }
                break;
            case INFO:
                if (logger.isInfoEnabled()) {
                    ft = MessageFormatter.format(format, arguments);
                }
                break;
            case WARN:
                if (logger.isWarnEnabled()) {
                    ft = MessageFormatter.format(format, arguments);
                }
                break;
            case ERROR:
                if (logger.isErrorEnabled()) {
                    ft = MessageFormatter.format(format, arguments);
                }
                break;
        }
        if (ft != null) {
            try {
                loggerStore.out(new InnerLog(ft.getMessage(), Exceptions.getStackTraceAsString(ft.getThrowable()), lv, LoggerContextUtil.getTraceId(), LoggerContextUtil.getSequenceId() + LoggerContextUtil.getSequenceIdLocal()));
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }
    }

}
