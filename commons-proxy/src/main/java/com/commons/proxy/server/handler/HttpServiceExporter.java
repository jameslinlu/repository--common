package com.commons.proxy.server.handler;

import com.alibaba.fastjson.JSON;
import com.commons.common.utils.ContextUtil;
import com.commons.log.utils.LoggerContextUtil;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.mq.Publish;
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
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.server.ServletServerHttpAsyncRequestControl;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationBasedExporter;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.scheduling.annotation.AsyncAnnotationAdvisor;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.ListenableFutureTask;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 远程服务http发布 异步非阻塞执行
 * Copyright (C)
 * RemoteServiceExporter
 * Author: jameslinlu
 */
public class HttpServiceExporter extends RemoteInvocationBasedExporter implements InitializingBean, HttpRequestHandler {

    private static Logger logger = LoggerFactory.getLogger(HttpServiceExporter.class);
    private String executorName = "taskExecutor";
    private BaseProxyConfig config;
    private IMQClient client;
    private String mqManagerKey;
    public HttpServiceExporter() {

    }

    public void setMqManagerKey(String mqManagerKey) {
        this.mqManagerKey = mqManagerKey;
    }

    public void setConfig(BaseProxyConfig config) {
        this.config = config;
    }

    public AsyncTaskExecutor getExecutor() {
        return ContextUtil.getBean(this.executorName, AsyncTaskExecutor.class);
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        logger.debug("Http Service handleRequest ");
        final ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        final ServletServerHttpRequest httpRequest = new ServletServerHttpRequest(request);
        final ServletServerHttpAsyncRequestControl asyncControl = new ServletServerHttpAsyncRequestControl(httpRequest, httpResponse);
        String timeout = httpRequest.getServletRequest().getHeader("timeout");
        final ListenableFutureTask<TransferResponseMessage> future = new ListenableFutureTask(new Callable<TransferResponseMessage>() {
            @Override
            public TransferResponseMessage call() throws Exception {
                try {
                    LoggerContextUtil.reset();
                    LoggerContextUtil.setTraceId(httpRequest.getServletRequest().getHeader(RequestTrace.PROXY_TRACE_ID));
                    LoggerContextUtil.setSequenceId(httpRequest.getServletRequest().getHeader(RequestTrace.PROXY_SEQUENCE_ID));
                    try {
                        RequestAuthorizeUtil.valid(httpRequest.getServletRequest(), 60 * 5);
                    } catch (ServiceException e) {
                        logger.info("Request Authorize Failed ,Headers {}", JSON.toJSONString(httpRequest.getHeaders()));
                        throw e;
                    }
                    byte[] inputBytes = IOUtils.toByteArray(httpRequest.getServletRequest().getInputStream());
                    TransferRequestMessage requestMessage = Serializer.deserialize(inputBytes);
                    RemoteInvocation invocation = new RemoteInvocation(requestMessage.getMethod(), requestMessage.getParameterTypes(), requestMessage.getParameters());
                    RemoteInvocationResult result = invokeAndCreateResult(invocation, getProxyForService());

                    TransferResponseMessage responseMessage = new TransferResponseMessage();


                    Method method = AopUtils.getTargetClass(getService()).getMethod(requestMessage.getMethod(), requestMessage.getParameterTypes());
                    Publish publish = method.getAnnotation(Publish.class);
                    if (publish != null) {
                        //标示Publish传递访问对象
                        BeanUtils.copyProperties(requestMessage, responseMessage);
                        Map<String, Object> header = new HashMap<>();
                        header.put(RequestAuthorize.PROXY_ID, request.getHeader(RequestAuthorize.PROXY_ID));
                        header.put(RequestAuthorize.PROXY_SIGN, request.getHeader(RequestAuthorize.PROXY_SIGN));
                        header.put(RequestAuthorize.PROXY_TIME, request.getHeader(RequestAuthorize.PROXY_TIME));
                        header.put(RequestTrace.PROXY_TRACE_ID, httpRequest.getServletRequest().getHeader(RequestTrace.PROXY_TRACE_ID));
                        header.put(RequestTrace.PROXY_SEQUENCE_ID, httpRequest.getServletRequest().getHeader(RequestTrace.PROXY_SEQUENCE_ID));

                        responseMessage.setPublish(true);
                        responseMessage.setAttributes(header);//for StateExchange
                    }
                    responseMessage.setMessageId(requestMessage.getMessageId());
                    responseMessage.setTraceId(requestMessage.getTraceId());
                    responseMessage.setResult(result.getValue());
                    responseMessage.setException(ServiceExporterUtil.wrapperException(result.getException()));
                    return responseMessage;
                } catch (Exception e) {
                    logger.error("Http ListenableFutureTask call throw Exception", e);
                    throw e;
                }

            }
        });

        future.addCallback(new ListenableFutureCallback<TransferResponseMessage>() {
            @Override
            public void onFailure(Throwable throwable) {
                logger.debug("Http ListenableFutureCallback onFailure {}", throwable);
                TransferResponseMessage error = new TransferResponseMessage();
                error.setException(ServiceExporterUtil.wrapperException(throwable));
                onSuccess(error);
            }

            @Override
            public void onSuccess(TransferResponseMessage responseMessage) {
                try {
                    logger.debug("Http ListenableFutureCallback onSuccess {}", responseMessage);
                    IOUtils.write(Serializer.serialize(responseMessage), httpResponse.getServletResponse().getOutputStream());
                    this.publish(responseMessage);
                } catch (IOException e) {
                    logger.error("Http ListenableFutureCallback onSuccess-IOException {}", e);
                } finally {
                    asyncControl.complete();
                }

            }

            public void publish(TransferResponseMessage responseMessage) {
                try {
                    if (responseMessage.getPublish() == null || !responseMessage.getPublish()) {
                        return;
                    }
                    logger.debug("Http Publish {}", responseMessage);
                    String exchangeName = responseMessage.getCluster().toUpperCase() + "_ExchangeState";
                    Message msg = MessageBuilder.withBody(Serializer.serialize(responseMessage)).build();
                    client.send(exchangeName, "", msg);
                } catch (Exception e) {
                    logger.error("Http Publish-IOException {}", e);
                }
            }
        });

        asyncControl.start(Float.valueOf(timeout).longValue());
        ((AsyncListenableTaskExecutor) getExecutor()).submitListenable(future);


    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.checkService();
        this.checkServiceInterface();
        this.setInterceptors(new Object[]{new AsyncAnnotationAdvisor(getExecutor(), new SimpleAsyncUncaughtExceptionHandler())});

        try {
            MQClientManager manager = ContextUtil.getBean(MQClientManager.class);
            client = manager.get(mqManagerKey);
        } catch (NoSuchBeanDefinitionException e) {
            //此异常针对不使用mq组件的场景
            client = null;
        }

        if (client != null && !(client instanceof RabbitMQClient)) {
            client = null;
            logger.debug("Not Support AnOther MqClient ,Please Change TO RabbitMQ");
        }
        if (client != null && client instanceof RabbitMQClient) {
            String exchangeName = config.getDomain().toUpperCase() + "_ExchangeState";
            client.declareExchange(exchangeName, false, null, RabbitMQClient.ExchangeMode.FANOUT);
        }
    }
}
