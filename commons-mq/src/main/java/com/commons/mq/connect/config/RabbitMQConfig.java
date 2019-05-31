package com.commons.mq.connect.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

/**
 * Copyright (C)
 * RabbitMQConfig
 * Author: jameslinlu
 */
public class RabbitMQConfig extends AbstractMQConfig {

    private String virtualHost;
    private int connectTimeout;
    private int closeTimeout;

    private CachingConnectionFactory.CacheMode cacheMode = CachingConnectionFactory.CacheMode.CHANNEL;

    private int channelCacheSize = 25;

    private int connectionCacheSize = 1;

    private int connectionLimit = Integer.MAX_VALUE;

    private int requestedHeartBeat = 60 * 1000;


    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getCloseTimeout() {
        return closeTimeout;
    }

    public void setCloseTimeout(int closeTimeout) {
        this.closeTimeout = closeTimeout;
    }

    public CachingConnectionFactory.CacheMode getCacheMode() {
        return cacheMode;
    }

    public void setCacheMode(CachingConnectionFactory.CacheMode cacheMode) {
        this.cacheMode = cacheMode;
    }

    public int getChannelCacheSize() {
        return channelCacheSize;
    }

    public void setChannelCacheSize(int channelCacheSize) {
        this.channelCacheSize = channelCacheSize;
    }

    public int getConnectionCacheSize() {
        return connectionCacheSize;
    }

    public void setConnectionCacheSize(int connectionCacheSize) {
        this.connectionCacheSize = connectionCacheSize;
    }

    public int getConnectionLimit() {
        return connectionLimit;
    }

    public void setConnectionLimit(int connectionLimit) {
        this.connectionLimit = connectionLimit;
    }

    public int getRequestedHeartBeat() {
        return requestedHeartBeat;
    }

    public void setRequestedHeartBeat(int requestedHeartBeat) {
        this.requestedHeartBeat = requestedHeartBeat;
    }
}
