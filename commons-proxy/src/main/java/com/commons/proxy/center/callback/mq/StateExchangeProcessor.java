package com.commons.proxy.center.callback.mq;

import com.commons.cache.util.LockUtil;
import com.commons.common.utils.ContextUtil;
import com.commons.common.utils.Identities;
import com.commons.common.utils.StringUtil;
import com.commons.log.utils.LoggerContextUtil;
import com.commons.metadata.model.mq.Consumer;
import com.commons.mq.client.IMQClient;
import com.commons.mq.client.MQClientManager;
import com.commons.mq.client.impl.RabbitMQClient;
import com.commons.proxy.center.model.RequestAuthorize;
import com.commons.proxy.center.model.RequestTrace;
import com.commons.proxy.center.secure.RequestAuthorizeUtil;
import com.commons.proxy.center.transfer.model.TransferResponseMessage;
import com.commons.proxy.serializable.Serializer;
import com.rabbitmq.client.Channel;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.util.MethodInvoker;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.ListenableFutureTask;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (C)
 * StateExchangeProcessor
 * Author: jameslinlu
 */
public class StateExchangeProcessor implements InitializingBean, DisposableBean, ChannelAwareMessageListener, BeanFactoryAware, ApplicationContextAware {
    private static Logger logger = LoggerFactory.getLogger(StateExchangeProcessor.class);

    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap(64));
    private ApplicationContext applicationContext;
    private BeanFactory beanFactory;
    private String executorName = "taskExecutor";
    private List<String> callbackExchangeNames;
    private MQClientManager mqClientManager;
    private String mqManagerKey;
    private IMQClient client;
    private SimpleMessageListenerContainer container;
    private final Map<String, List<String>> cacheExecuteKeyMap = new HashMap<>();
    private final static Integer LOCK_WAIT = 1;
    private final static Integer LOCK_RELEASE = 5;

    public void setMqClientManager(MQClientManager mqClientManager) {
        this.mqClientManager = mqClientManager;
    }

    public void setCallbackExchangeNames(List<String> callbackExchangeNames) {
        this.callbackExchangeNames = callbackExchangeNames;
    }

    public void setMqManagerKey(String mqManagerKey) {
        this.mqManagerKey = mqManagerKey;
    }

    public AsyncTaskExecutor getExecutor() {
        return ContextUtil.getBean(this.executorName, AsyncTaskExecutor.class);
    }

    public void setExecutorName(String executorName) {
        this.executorName = executorName;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onMessage(final Message message, final Channel channel) throws Exception {
        //按广播寻找本地实现类
        logger.info("Exchange State On Message: {}", message);
        final ListenableFutureTask<TransferResponseMessage> future = new ListenableFutureTask(new Callable<TransferResponseMessage>() {
            @Override
            public TransferResponseMessage call() throws Exception {
                try {
                    LoggerContextUtil.reset();
                    byte[] inputBytes = message.getBody();
                    TransferResponseMessage responseMessage = Serializer.deserialize(inputBytes);
                    String methodKey = responseMessage.getInterfaze() + "." + responseMessage.getMethod();
                    if (cacheExecuteKeyMap.get(methodKey) == null || cacheExecuteKeyMap.get(methodKey).isEmpty()) {
                        logger.debug("Exchange State No Handler :{}", methodKey);
                        return responseMessage;
                    }

                    String traceId = String.valueOf(responseMessage.getAttributes().get(RequestTrace.PROXY_TRACE_ID));
                    String seqId = String.valueOf(responseMessage.getAttributes().get(RequestTrace.PROXY_SEQUENCE_ID));
                    String proxyId = String.valueOf(responseMessage.getAttributes().get(RequestAuthorize.PROXY_ID));
                    String proxySign = String.valueOf(responseMessage.getAttributes().get(RequestAuthorize.PROXY_SIGN));
                    String proxyTime = String.valueOf(responseMessage.getAttributes().get(RequestAuthorize.PROXY_TIME));
                    LoggerContextUtil.setTraceId(traceId);
                    LoggerContextUtil.setSequenceId(seqId);
                    RequestAuthorizeUtil.valid(proxyId, proxySign, proxyTime, -1);

                    Consumer.State state = null;
                    if (message.getMessageProperties() != null && message.getMessageProperties().getHeaders() != null && message.getMessageProperties().getHeaders().get("x-death") != null) {
                        String reason = ((Map) ((List) message.getMessageProperties().getHeaders().get("x-death")).get(0)).get("reason").toString();
                        if (reason.equalsIgnoreCase("rejected") || reason.equalsIgnoreCase("expired")) {
                            state = Consumer.State.TIMEOUT;
                        }
                        if (reason.equalsIgnoreCase("maxlen")) {
                            state = Consumer.State.OVERFLOW;
                        }
                    }
                    if (state == null && responseMessage.getException() != null) {
                        state = Consumer.State.FAIL;
                    } else if (state == null) {
                        state = Consumer.State.SUCCESS;
                    }

                    Class[] types = responseMessage.getParameterTypes();
                    types = Arrays.copyOf(types, types.length + 1);
                    types[types.length - 1] = Consumer.State.class;
                    Object[] parameters = responseMessage.getParameters();
                    parameters = Arrays.copyOf(parameters, parameters.length + 3);
                    parameters[parameters.length - 3] = state;
                    parameters[parameters.length - 2] = responseMessage.getResult();
                    parameters[parameters.length - 1] = responseMessage.getException();
                    Exception lastException = null;
                    List<String> methods = cacheExecuteKeyMap.get(methodKey);
                    if (methods == null || methods.isEmpty()) {
                        return null;
                    }
                    for (String targetKey : methods) {
                        String[] targets = targetKey.split("\\.");
                        Object targetBean = beanFactory.getBean(targets[0]);
                        String targetMethod = targets[1];
                        Consumer.State targetState = Consumer.State.valueOf(targets[2]);
                        String lockIdentified = null;
                        if (targets.length > 3) {
                            lockIdentified = targets[3];//加锁标示
                        }
                        //目标方法指定非ALL && 接受方法和目标方法State不一致  == 跳过
                        if (targetState != Consumer.State.ALL && !targetState.getValue().equals(state.getValue())) {
                            logger.debug("State Exchange Not Provider Process {} {} with {}", targetBean, targetMethod, state);
                            continue;
                        }
                        if (!StringUtil.isEmpty(lockIdentified)) {
                            //加锁标示非空  根据messageId + lock标示  来锁定方法，lock标示用于例如web层不锁定,service锁定只执行一次
                            RLock lock = null;
                            try {
                                lock = LockUtil.getInstance().getLock(responseMessage.getMessageId() + "_" + lockIdentified);
                                boolean locked = lock.tryLock(LOCK_WAIT, LOCK_RELEASE, TimeUnit.SECONDS);
                                if (!locked) {
                                    //未被锁定 跳过执行方法
                                    continue;
                                }
                            } catch (InterruptedException e) {
                                logger.error("Lock Method Fail {} {}", targets, e);
                                if (lock != null) {
                                    lock.forceUnlock();
                                }
                            }
                        }
                        try {
                            MethodInvoker beanMethod = new MethodInvoker();
                            beanMethod.setTargetObject(targetBean);
                            beanMethod.setTargetMethod(targetMethod);
                            beanMethod.setArguments(parameters);
                            beanMethod.prepare();
                            beanMethod.invoke();
                        } catch (Exception e) {
                            logger.error("State Exchange Invoke Fail : {} ", targetKey, e);
                            lastException = e;
                        }
                    }
                    if (lastException != null) {
                        throw lastException;
                    }
                    return responseMessage;
                } catch (Exception e) {
                    logger.error("MQ State call throw Exception {}", e);
                    throw e;
                }
            }
        });

        future.addCallback(new ListenableFutureCallback<TransferResponseMessage>() {
            @Override
            public void onFailure(Throwable throwable) {
                logger.error("MQ State  onFailure {}", throwable);
            }

            @Override
            public void onSuccess(TransferResponseMessage responseMessage) {
                logger.debug("MQ State  onSuccess {}", responseMessage);
            }
        });

        ((AsyncListenableTaskExecutor) getExecutor()).submitListenable(future);

    }


    @Override
    public void destroy() throws Exception {
        if (client instanceof RabbitMQClient) {
            container.destroy();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (final String beanName : this.applicationContext.getBeanDefinitionNames()) {
            final Object bean = this.applicationContext.getBean(beanName);
            Class targetClass = AopUtils.getTargetClass(bean);
            if (!this.nonAnnotatedClasses.contains(targetClass)) {
                final LinkedHashSet annotatedMethods = new LinkedHashSet(1);
                ReflectionUtils.doWithMethods(targetClass, new ReflectionUtils.MethodCallback() {
                    public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                        Consumer annotation = AnnotationUtils.getAnnotation(method, Consumer.class);
                        if (annotation != null) {
                            processConsumer(annotation, beanName, method.getName());
                            annotatedMethods.add(method);
                        }
                    }
                });
                if (annotatedMethods.isEmpty()) {
                    this.nonAnnotatedClasses.add(targetClass);
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("No @Consumer annotations found on bean class: " + bean.getClass());
                    }
                } else if (this.logger.isDebugEnabled()) {
                    this.logger.debug(annotatedMethods.size() + " No @Consumer methods processed on bean \'" + bean.getClass() + "\': " + annotatedMethods);
                }
            }
        }
        if (cacheExecuteKeyMap == null || cacheExecuteKeyMap.isEmpty()) {
            //无需处理回调方法 不注册监听
            return;
        }

        client = mqClientManager.get(mqManagerKey);
        if (client instanceof RabbitMQClient) {
            String tmpQueue = Identities.getRandomGUID(true) + "_Queue";
            client.declareQueue(tmpQueue, false, true, null);
            for (String fanoutExchangeName : callbackExchangeNames) {
                String exchangeName = fanoutExchangeName.toUpperCase() + "_ExchangeState";
                client.declareExchange(exchangeName, false, null, RabbitMQClient.ExchangeMode.FANOUT);
                client.declareBind(tmpQueue, exchangeName, RabbitMQClient.ExchangeMode.FANOUT, null);
            }
            container = new SimpleMessageListenerContainer(((RabbitMQClient) client).getConnectionFactory());
            container.setMessageListener(this);
            container.setAcknowledgeMode(AcknowledgeMode.AUTO);
            container.setQueues(new Queue(tmpQueue));
            container.setRecoveryInterval(2 * 1000);
            container.start();
        }
    }

    public void processConsumer(Consumer consumer, String beanName, String methodName) {
        //缓存bean和method 对应 接口和方法名  的关系
        String key = consumer.serviceInterface().toString().split(" ")[1] + "." + consumer.method();
        String state = consumer.state().getValue();
        String lockIdentified = consumer.scope() == null ? "" : consumer.scope();
        if (cacheExecuteKeyMap.get(key) == null) {
            cacheExecuteKeyMap.put(key, new ArrayList<>());
        }
        //xxService . xxMethod . State
        cacheExecuteKeyMap.get(key).add(beanName + "." + methodName + "." + state + "." + lockIdentified);
    }

}
