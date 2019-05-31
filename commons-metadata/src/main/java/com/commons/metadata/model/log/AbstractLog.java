package com.commons.metadata.model.log;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright (C)
 * AbstractLog
 * Author: jameslinlu
 */
public abstract class AbstractLog implements Serializable {

    //    记录TraceId ：首次发起生成唯一ID,传递每次调用
    private String traceId;
    //    记录SequenceId : 请求序号 入口为0 ，若执行调用则递增
    private String sequenceId;
    //    记录抛出异常
    private String exception;
    //    记录手动日志信息
    private String message;
    //    记录时间
    private Date time;
    // ES doc _id
    private String id;
    // 日志可读性描述
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 日志级别
     *
     * @return
     */
    public abstract String getLevel();

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}
