package com.commons.mq.connect.factory;

import com.commons.mq.client.IMQClient;
import com.commons.mq.client.impl.RabbitMQClient;
import com.commons.mq.connect.config.AbstractMQConfig;
import com.commons.mq.connect.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

/**
 * Copyright (C)
 * CommonMQClientFactory
 * Author: jameslinlu
 */
public class CommonMQClientFactory {

    public static IMQClient newClient(AbstractMQConfig config) {
        if (config instanceof RabbitMQConfig) {
            RabbitMQConfig rabbitMQConfig = (RabbitMQConfig) config;
            CachingConnectionFactory cf = new CachingConnectionFactory();
            cf.setAddresses(rabbitMQConfig.getAddresses());
            cf.setUsername(rabbitMQConfig.getUsername());
            cf.setPassword(rabbitMQConfig.getPassword());
            cf.setVirtualHost(rabbitMQConfig.getVirtualHost());
            cf.setConnectionTimeout(rabbitMQConfig.getConnectTimeout());
            cf.setCloseTimeout(rabbitMQConfig.getCloseTimeout());
            cf.setRequestedHeartBeat(rabbitMQConfig.getRequestedHeartBeat());
            cf.setCacheMode(rabbitMQConfig.getCacheMode());
            cf.setChannelCacheSize(rabbitMQConfig.getChannelCacheSize());
            cf.setConnectionCacheSize(rabbitMQConfig.getConnectionCacheSize());
            return new RabbitMQClient(cf);
        }
        return null;
    }
}
