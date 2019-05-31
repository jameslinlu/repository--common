package com.commons.common.support.spring.zookeeper;

import org.apache.curator.RetryPolicy;

import java.io.Serializable;

/**
 * Copyright (C)
 * ZookeeperConfiguration
 * Author: jameslinlu
 */
public class ZookeeperConfiguration implements Serializable {
    private String connectString;
    private int sessionTimeoutMs = 20 * 1000;
    private int connectionTimeoutMs = 15 * 1000;
    private RetryPolicy retryPolicy = new ZookeeperRetryForever(3 * 1000);

    public String getConnectString() {
        return connectString;
    }

    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    public int getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }

    public void setSessionTimeoutMs(int sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public int getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public void setConnectionTimeoutMs(int connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }
}
