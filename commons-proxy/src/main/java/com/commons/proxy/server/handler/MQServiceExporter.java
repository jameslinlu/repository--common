package com.commons.proxy.server.handler;

import com.commons.common.utils.ContextUtil;
import com.commons.log.utils.LoggerContextUtil;
import com.commons.metadata.exception.ServiceException;
import com.commons.mq.client.IMQClient;
import com.commons.mq.client.MQClientManager;
import com.commons.mq.client.impl.RabbitMQClient;
import com.commons.proxy.center.config.BaseProxyConfig;
import com.commons.proxy.center.model.RequestAuthorize;
import com.commons.proxy.center.model.RequestTrace;
import com.commons.proxy.center.secure.RequestAuthorizeUtil;
import com.commons.proxy.center.transfer.model.TransferRequestMessage;
import com.commons.proxy.center.transfer.model.TransferResponseMessage;
import com.commons.proxy.serializable.Serializer;
import com.commons.proxy.server.utils.ServiceExporterUtil;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationBasedExporter;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.scheduling.annotation.AsyncAnnotationAdvisor;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.ListenableFutureTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * MQ业务处理服务类
 * Copyright (C)
 * MQServiceExporter
 * Author: jameslinlu
 */
public class MQServiceExporter extends RemoteInvocationBasedExporter implements InitializingBean, DisposableBean, ChannelAwareMessageListener {

    private static Logger logger = LoggerFactory.getLogger(MQServiceExporter.class);

    private String executorName = "taskExecutor";
    private IMQClient client;
    private SimpleMessageListenerContainer container;
    private float timeout;//队列超时时间
    private float delay;//队列超时时间
    private BaseProxyConfig config;
    private String mqManagerKey;

    public MQServiceExporter() {
    }

    public void setMqManagerKey(String mqManagerKey) {
        this.mqManagerKey = mqManagerKey;
    }

    public void setDelay(float delay) {
        this.delay = delay;
    }

    public void setTimeout(float timeout) {
        this.timeout = timeout;
    }

    public void setConfig(BaseProxyConfig config) {
        this.config = config;
    }

    public AsyncTaskExecutor getExecutor() {
        return ContextUtil.getBean(this.executorName, AsyncTaskExecutor.class);
    }

    @Override
    public void onMessage(final Message message, final Channel channel) {

        try {
            // when no consume handle and overtime, send to dead exchange
            logger.debug("MQ Consumer Handler Message : {}", message);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            logger.error("MQ Consume Basic ACK  Fail", e);
        }

        final ListenableFutureTask<TransferResponseMessage> future = new ListenableFutureTask(new Callable<TransferResponseMessage>() {
            @Override
            public TransferResponseMessage call() throws Exception {
                try {
                    LoggerContextUtil.reset();
                    byte[] inputBytes = message.getBody();
                    TransferRequestMessage requestMessage = Serializer.deserialize(inputBytes);
                    String traceId = String.valueOf(requestMessage.getAttributes().get(RequestTrace.PROXY_TRACE_ID));
                    String seqId = String.valueOf(requestMessage.getAttributes().get(RequestTrace.PROXY_SEQUENCE_ID));
                    String proxyId = String.valueOf(requestMessage.getAttributes().get(RequestAuthorize.PROXY_ID));
                    String proxySign = String.valueOf(requestMessage.getAttributes().get(RequestAuthorize.PROXY_SIGN));
                    String proxyTime = String.valueOf(requestMessage.getAttributes().get(RequestAuthorize.PROXY_TIME));
                    LoggerContextUtil.setTraceId(traceId);
                    LoggerContextUtil.setSequenceId(seqId);
                    RequestAuthorizeUtil.valid(proxyId, proxySign, proxyTime, 60);
                    RemoteInvocation invocation = new RemoteInvocation(requestMessage.getMethod(), requestMessage.getParameterTypes(), requestMessage.getParameters());
                    RemoteInvocationResult result = invokeAndCreateResult(invocation, getProxyForService());
                    TransferResponseMessage responseMessage = new TransferResponseMessage();
                    BeanUtils.copyProperties(requestMessage, responseMessage);
                    responseMessage.setResult(result.getValue());
                    responseMessage.setException(ServiceExporterUtil.wrapperException(result.getException()));
                    return responseMessage;
                } catch (Exception e) {
                    logger.error("MQ Handler Call throw Exception {}", e);
                    throw e;
                }
            }
        });

        future.addCallback(new ListenableFutureCallback<TransferResponseMessage>() {
            @Override
            public void onFailure(Throwable throwable) {
                logger.debug("MQ On Handler onFailure {}", throwable);
                byte[] inputBytes = message.getBody();
                TransferResponseMessage error = Serializer.deserialize(inputBytes);
                error.setException(ServiceExporterUtil.wrapperException(throwable));
                onSuccess(error);
            }

            @Override
            public void onSuccess(TransferResponseMessage responseMessage) {
                try {
                    logger.debug("MQ  On Handler onSuccess {}", responseMessage);
                    String exchangeName = responseMessage.getCluster().toUpperCase() + "_ExchangeState";
                    Message msg = MessageBuilder.withBody(Serializer.serialize(responseMessage)).build();
                    client.send(exchangeName, "", msg);
                } catch (Exception e) {
                    logger.error("MQ  On Handler onSuccess-IOException {}", e);
                }
            }
        });

        ((AsyncListenableTaskExecutor) getExecutor()).submitListenable(future);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.checkService();
        this.checkServiceInterface();
        this.setInterceptors(new Object[]{new AsyncAnnotationAdvisor(getExecutor(), new SimpleAsyncUncaughtExceptionHandler())});
        MQClientManager manager = ContextUtil.getBean(MQClientManager.class);
        client = manager.get(mqManagerKey);
        if (client != null && client instanceof RabbitMQClient) {
            String prefixDeclareName = config.getDomain().toUpperCase() + "." + getServiceInterface().toString().split(" ")[1];
            String queueName = prefixDeclareName + "Queue";
            String exchangeName = config.getDomain().toUpperCase() + "_Exchange";
            String routeKey = prefixDeclareName + ".*";
            if (delay > 0) {
                exchangeName += "Delay";//区分2个 Exchange
            }
            checkDeclare(queueName, exchangeName, routeKey, timeout, delay);
            container = new SimpleMessageListenerContainer(((RabbitMQClient) client).getConnectionFactory());
            container.setMessageListener(this);
            container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
            container.setQueues(new Queue(queueName));
            container.setRecoveryInterval(2000);
            container.start();
        }
    }

    private void checkDeclare(String queueName, String exchangeName, String routeKey, float timeout, float delay) throws ServiceException {

        if (delay > 0) {
            client.declareExchange(exchangeName, true, new HashMap<>(), RabbitMQClient.ExchangeMode.DELAY);
            client.declareQueue(queueName, true, new HashMap<>());
            client.declareBind(queueName, exchangeName, RabbitMQClient.ExchangeMode.DELAY, routeKey);
        } else {
            client.declareExchange(exchangeName, true, null, RabbitMQClient.ExchangeMode.TOPIC);
            Map<String, Object> args = new HashMap<>();
            if (timeout > 0) {
                //State = 状态
                args.put("x-dead-letter-exchange", exchangeName + "State");//domain + Exchange + State =CGPOSS_ExchangeDead
                args.put("x-message-ttl", Float.valueOf(timeout).intValue());
            }
            client.declareQueue(queueName, true, args);
            client.declareBind(queueName, exchangeName, RabbitMQClient.ExchangeMode.TOPIC, routeKey);
        }

    }

    @Override
    public void destroy() throws Exception {
        if (client instanceof RabbitMQClient) {
            container.destroy();
        }
    }
}
