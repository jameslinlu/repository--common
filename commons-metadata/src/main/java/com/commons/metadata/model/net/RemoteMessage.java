package com.commons.metadata.model.net;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C)
 * RemoteMessage
 * Author: jameslinlu
 */
public class RemoteMessage implements Serializable {

    //回调业务键 - 为相同工程中可接受系统事件通知  - 应自定保证唯一业务含义
    private String routeKey;
    //主机地址
    private String hostname;
    //端口
    private Integer port;
    //用户名
    private String username;
    //密码
    private String password;
    //命令集
    private List<String> commands;
    //交互反馈内容-需自行解析
    private String content;
    //异常内容
    private Throwable exception;
    //扩展参数
    private Map<String, Object> attributes;

    public String getRouteKey() {
        return routeKey;
    }

    public void setRouteKey(String routeKey) {
        this.routeKey = routeKey;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
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

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
