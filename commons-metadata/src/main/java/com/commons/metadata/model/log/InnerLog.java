package com.commons.metadata.model.log;


import com.commons.metadata.model.log.enums.LoggerLevel;

/**
 * Copyright (C)
 * InnerLog
 * Author: jameslinlu
 */
public class InnerLog extends BasicLog {

    public InnerLog() {
    }

    public InnerLog(String message, String exception, LoggerLevel level, String traceId, String sequenceId) {
        super.setMessage(message);
        super.setException(exception);
        super.setLevel(level.toString());
        super.setTraceId(traceId);
        super.setSequenceId(sequenceId);
    }
}
