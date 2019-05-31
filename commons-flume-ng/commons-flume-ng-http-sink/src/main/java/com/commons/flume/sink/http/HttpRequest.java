package com.commons.flume.sink.http;

import org.apache.http.entity.AbstractHttpEntity;

import java.util.Map;

/**
 * Copyright (C)
 * HttpRequest
 * Author: jameslinlu
 */
public class HttpRequest {
    private String method;//POST|GET 等等
    private String url;
    private Map<String, Object> params;
    private AbstractHttpEntity entity;
    private Map<String, String> headers;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public AbstractHttpEntity getEntity() {
        return entity;
    }

    public void setEntity(AbstractHttpEntity entity) {
        this.entity = entity;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
