package com.commons.metadata.model.log;

/**
 * Copyright (C)
 * AccessLog
 * Author: jameslinlu
 */
public class AccessLog extends BasicLog {

    //    记录客户端IP地址
    private String ip;
    //    记录请求的URL和HTTP协议
    private String request;
    //    记录请求状态码
    private Integer httpStatus;
    //    记录从哪个页面链接访问过来的
    private String referLink;
    //    记录客户端浏览器相关信息
    private String userAgent;
    //    记录请求的长度
    private Integer requestLength;
    //    记录请求方法
    private String requestMethod;

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getReferLink() {
        return referLink;
    }

    public void setReferLink(String referLink) {
        this.referLink = referLink;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Integer getRequestLength() {
        return requestLength;
    }

    public void setRequestLength(Integer requestLength) {
        this.requestLength = requestLength;
    }

}
