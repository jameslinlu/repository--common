package com.commons.mq.client;

import com.commons.metadata.exception.ServiceException;
import com.commons.mq.client.impl.RabbitMQClient;
import org.springframework.amqp.core.Message;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * Copyright (C)
 * IMQClient
 * Author: jameslinlu
 */
public interface IMQClient {

    void initialize(ApplicationContext applicationContext);

    void destroy();

    void declareQueue(String name, boolean duration, Map<String, Object> args) throws ServiceException;

    void declareQueue(String name, boolean duration, boolean autoDelete, Map<String, Object> args) throws ServiceException;

    void declareExchange(String name, boolean duration, Map<String, Object> args, RabbitMQClient.ExchangeMode mode) throws ServiceException;

    void declareBind(String queueName, String exchangeName, RabbitMQClient.ExchangeMode mode, String routingKey) throws ServiceException;

    void send(String exchangeName, String routeKey, Message msg);
}
