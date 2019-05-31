package com.commons.mq.client.impl;

import com.commons.metadata.exception.ServiceException;
import com.commons.mq.client.IMQClient;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C)
 * RabbitMQClient
 * Author: jameslinlu
 */
public class RabbitMQClient implements IMQClient {

    private RabbitAdmin rabbitAdmin;
    private CachingConnectionFactory connectionFactory;

    public RabbitAdmin getRabbitAdmin() {
        return rabbitAdmin;
    }

    public CachingConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public enum ExchangeMode {
        TOPIC,
        DIRECT,
        FANOUT,
        DELAY
    }

    public RabbitMQClient(CachingConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }


    public void initialize(ApplicationContext applicationContext) {
        this.connectionFactory.start();
        this.rabbitAdmin = new RabbitAdmin(connectionFactory);
        if (applicationContext != null) {
            this.rabbitAdmin.setApplicationContext(applicationContext);
            this.rabbitAdmin.initialize();
        }
    }

    public void destroy() {
        this.rabbitAdmin = null;
        this.connectionFactory.destroy();
    }

    public void declareQueue(String name, boolean duration, Map<String, Object> args) throws ServiceException {
        this.declareQueue(name, duration, false, args);
    }

    @Override
    public void declareQueue(String name, boolean duration, boolean autoDelete, Map<String, Object> args) throws ServiceException {
        rabbitAdmin.declareQueue(new Queue(name, duration, false, autoDelete, args));
    }

    public void declareExchange(String name, boolean duration, Map<String, Object> args, ExchangeMode mode) throws ServiceException {
        switch (mode) {
            case TOPIC:
                rabbitAdmin.declareExchange(new TopicExchange(name, duration, false, args));
                break;
            case FANOUT:
                rabbitAdmin.declareExchange(new FanoutExchange(name, duration, false, args));
                break;
            case DIRECT:
                rabbitAdmin.declareExchange(new DirectExchange(name, duration, false, args));
                break;
            case DELAY:
                //x-delayed-message 定义延迟队列   x-delayed-type定义底层交换类型
                if (args == null) {
                    args = new HashMap<>();
                }
                if (args.get("x-delayed-type") == null) {
                    args.put("x-delayed-type", "topic");
                }
                rabbitAdmin.declareExchange(new CustomExchange(name, "x-delayed-message", duration, false, args));
                break;
        }
    }

    public void declareBind(String queueName, String exchangeName, ExchangeMode mode, String routingKey) throws ServiceException {
        switch (mode) {
            case TOPIC:
                rabbitAdmin.declareBinding(BindingBuilder.bind(new Queue(queueName)).to(new TopicExchange(exchangeName)).with(routingKey));
                break;
            case FANOUT:
                rabbitAdmin.declareBinding(BindingBuilder.bind(new Queue(queueName)).to(new FanoutExchange(exchangeName)));
                break;
            case DIRECT:
                rabbitAdmin.declareBinding(BindingBuilder.bind(new Queue(queueName)).to(new DirectExchange(exchangeName)).with(routingKey));
                break;
            case DELAY:
                Exchange delayExchange = new CustomExchange(exchangeName, "x-delayed-message");
                rabbitAdmin.declareBinding(BindingBuilder.bind(new Queue(queueName)).to(delayExchange).with(routingKey).noargs());
                break;
        }

    }


    @Override
    public void send(String exchangeName, String routeKey, Message msg) {
        rabbitAdmin.getRabbitTemplate().send(exchangeName, routeKey, msg);
    }
}
