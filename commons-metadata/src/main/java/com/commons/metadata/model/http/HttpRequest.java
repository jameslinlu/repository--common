package com.commons.metadata.model.http;

import java.io.Serializable;
import java.util.Map;

/**
 * 接口请求的载体POJO
 * Copyright (C)
 * HttpRequest
 * Author: jameslinlu
 */
public class HttpRequest implements Serializable {
    public enum Method {
        POST,
        GET,
        DELETE,
        PUT
    }

    private Method method;//post get  delete put
    private String host;//192.168.1.1:223
    private String url;//    http://%s/api/xxx
    private String username; // 按需
    private String password;// 按需
    private Map<String, Object> params;//entity类型则在内部转换为json 并结合method 使用
    private Map<String, String> headers;// 按需

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
