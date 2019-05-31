package com.commons.proxy.center.transfer.impl;

import com.commons.common.utils.ContextUtil;
import com.commons.metadata.exception.ServiceException;
import com.commons.mq.client.IMQClient;
import com.commons.mq.client.MQClientManager;
import com.commons.mq.client.impl.RabbitMQClient;
import com.commons.proxy.center.model.ProxyConfigure;
import com.commons.proxy.center.model.RequestAuthorize;
import com.commons.proxy.center.model.RequestTrace;
import com.commons.proxy.center.transfer.IProxyTransfer;
import com.commons.proxy.center.transfer.model.TransferRequestMessage;
import com.commons.proxy.center.transfer.model.TransferResponseMessage;
import com.commons.proxy.serializable.Serializer;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePropertiesBuilder;

import java.util.*;

/**
 * Copyright (C)
 * IProxyTransfer
 * Author: jameslinlu
 */
public class MQProxyTransfer implements IProxyTransfer {

    private static MQProxyTransfer transfer = null;
    private IMQClient client = null;

    public static IProxyTransfer getInstance(ProxyConfigure config) {
        if (transfer == null) {
            synchronized (MQProxyTransfer.class) {
                transfer = new MQProxyTransfer(config);
            }
        }
        return transfer;
    }

    public MQProxyTransfer(ProxyConfigure config) {
        if (client == null) {
            MQClientManager mqClientManager = ContextUtil.getBean(MQClientManager.class);
            client = mqClientManager.get(config.getMqManagerKey());
        }
    }
    /**
    public static void main(String[] args) throws Exception {
        延迟队列测试代码
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.130.6");
        factory.setUsername("zyuc");
        factory.setPassword("zyuc");
        factory.setPort(5672);
        factory.setVirtualHost("/zyuc");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        Map<String, Object> argsExchange = new HashMap<>();
        argsExchange.put("x-delayed-type", "topic");//direct
        channel.exchangeDeclare("my-exchange", "x-delayed-message", true, false, argsExchange);

        channel.queueDeclare("my-queue", true, false, false, null);
        channel.queueBind("my-queue", "my-exchange", "test.*");

        byte[] messageBodyBytes = (System.currentTimeMillis() + "").getBytes("UTF-8");
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("x-delay", 5 * 1000);
        AMQP.BasicProperties.Builder props = new AMQP.BasicProperties.Builder().headers(headers);
        channel.basicPublish("my-exchange", "test.a", props.build(), messageBodyBytes);

        byte[] messageBodyBytes2 = (System.currentTimeMillis() + "").getBytes("UTF-8");
        Map<String, Object> headers2 = new HashMap<String, Object>();
        headers2.put("x-delay", 10 * 1000);
        AMQP.BasicProperties.Builder props2 = new AMQP.BasicProperties.Builder().headers(headers2);
        channel.basicPublish("my-exchange", "hello.b", props2.build(), messageBodyBytes2);


        //创建队列消费者
        QueueingConsumer consumer = new QueueingConsumer(channel);
        //指定消费队列
        channel.basicConsume("my-queue", true, consumer);
        while (true) {
            //nextDelivery是一个阻塞方法（内部实现其实是阻塞队列的take方法）
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            System.out.println(" [x] Received '" + (System.currentTimeMillis() - Long.valueOf(message)) + "'");
        }

    }
     */

    public TransferResponseMessage send(TransferRequestMessage requestMessage) throws ServiceException {

        Map<String, Object> header = new HashMap<>();
        header.put(RequestAuthorize.PROXY_ID, requestMessage.getMessageId());
        header.put(RequestAuthorize.PROXY_SIGN, requestMessage.getSignature());
        header.put(RequestAuthorize.PROXY_TIME, requestMessage.getTimestamp());
        header.put(RequestTrace.PROXY_TRACE_ID, requestMessage.getTraceId());
        header.put(RequestTrace.PROXY_SEQUENCE_ID, requestMessage.getSequenceId());
        requestMessage.setAttributes(header);//mq set attr for isValid transfer authorize
        MessageBuilder builder = MessageBuilder.withBody(Serializer.serialize(requestMessage));
        MessagePropertiesBuilder propertiesBuilder = MessagePropertiesBuilder.newInstance();
        propertiesBuilder
//              .setAppId(TO-DO) flag for war
                .setClusterId(requestMessage.getCluster())
                .setContentType("application/octet-stream")
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .setMessageId(requestMessage.getMessageId())
                .setTimestamp(new Date(Long.valueOf(requestMessage.getTimestamp())));
        if (requestMessage.getDelay() > 0) {
            propertiesBuilder.setHeader("x-delay", Float.valueOf(requestMessage.getDelay()).intValue());
        }
        Message msg = builder
                .andProperties(propertiesBuilder.build())
                .build();
        //CGPBOSS.com.zyuc.xxx.service.xxxService
        String prefixDeclareName = requestMessage.getCluster().toUpperCase() + "." + requestMessage.getInterfaze();
        String queueName = prefixDeclareName + "Queue";
        String exchangeName = requestMessage.getCluster().toUpperCase() + "_Exchange";
        String routeKey = prefixDeclareName + ".*";
        if (requestMessage.getDelay() > 0) {
            exchangeName += "Delay";//区分2个 Exchange
        }
        checkDeclare(queueName, exchangeName, routeKey, requestMessage.getTimeout(), requestMessage.getDelay());
        client.send(exchangeName, routeKey, msg);
        return new TransferResponseMessage();//MQ return null
    }

    /**
     * 防止未创建声明检查
     *
     * @param queueName 队列
     * @param exchangeName 交换
     * @param routeKey 路由
     * @throws ServiceException
     */
    private List<String> declares = new ArrayList<>();

    private void checkDeclare(String queueName, String exchangeName, String routeKey, float timeout, float delay) throws ServiceException {
        if (!declares.contains("exchangeName")) {
            if (delay > 0) {
                client.declareExchange(exchangeName, true, new HashMap<>(), RabbitMQClient.ExchangeMode.DELAY);
            } else {
                client.declareExchange(exchangeName, true, new HashMap<>(), RabbitMQClient.ExchangeMode.TOPIC);
            }
        }
        if (!declares.contains("queueName")) {
            Map<String, Object> args = new HashMap<>();
            if (delay <= 0 && timeout > 0) {
                //State = 状态
                args.put("x-dead-letter-exchange", exchangeName + "State");//domain + Exchange + State =CGPOSS_ExchangeDead
                //args.put("x-dead-letter-routing-key", "");
                args.put("x-message-ttl", Float.valueOf(timeout).intValue());
            }
            client.declareQueue(queueName, true, args);
            if (delay > 0) {
                client.declareBind(queueName, exchangeName, RabbitMQClient.ExchangeMode.DELAY, routeKey);
            } else {
                client.declareBind(queueName, exchangeName, RabbitMQClient.ExchangeMode.TOPIC, routeKey);
            }
        }
    }

}
