package com.commons.metadata.model.log;

import java.util.Date;
import java.util.Map;

/**
 * Copyright (C)
 * BaseLog
 * Author: jameslinlu
 */
public class BasicLog extends AbstractLog {

    //    记录类型:Access Proxy-Http-Kryo
    private String type;
    //    记录请求时间
    private Date begin;
    //    记录持续时长
    private Long spendMs;
    //    记录结束时间
    private Date end;
    //    记录集群
    private String domain;
    //    记录目标方法
    private String method;
    //    记录方法参数
    private String arguments;
    //    记录方法返回
    private String returnVal;
    //    日志级别
    private String level;
    //    记录
    private Map<String, Object> attributes;

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Long getSpendMs() {
        return spendMs;
    }

    public void setSpendMs(Long spendMs) {
        this.spendMs = spendMs;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public String getReturnVal() {
        return returnVal;
    }

    public void setReturnVal(String returnVal) {
        this.returnVal = returnVal;
    }


}
