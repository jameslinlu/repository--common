package com.commons.configuration.model;

import java.util.List;

/**
 * Copyright (C)
 * CommonConfigBundle
 * Author: jameslinlu
 */
public class CommonConfigBundle {

    private String domain;
    private String ip;
    private String port;
    //参数组 user.default 逻辑标识,对于properties文件则为文件名,除后缀 . 相当于节点名
    private String group;
    private List<CommonConfig> configs;
    //固定的有效期 超过有效期为更新则会cleanup删除 默认24小时有效 检查时间为1小时
    private Long expire;
    private boolean persistent;

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public Long getExpire() {
        return expire;
    }

    public void setExpire(Long expire) {
        this.expire = expire;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public List<CommonConfig> getConfigs() {
        return configs;
    }

    public void setConfigs(List<CommonConfig> configs) {
        this.configs = configs;
    }
}
