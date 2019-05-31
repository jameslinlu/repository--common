package com.commons.common.net.telnet;

import com.commons.common.net.telnet.impl.CommonTelnetClient;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.net.TelnetMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Telnet业务的Producer 单例执行 每个IP一个队列
 * 队列处理回调@Subscribe TelnetEvent, CommonEvent.register(service)
 * Copyright (C)
 * TelnetQueue
 * Author: jameslinlu
 */
public class TelnetQueue {

    private static final Logger logger = LoggerFactory.getLogger(TelnetQueue.class);

    public Map<String, LinkedBlockingQueue> telnetQueues = new ConcurrentHashMap<>();
    private static final Integer QUEUE_SIZE = 5;

    private TelnetQueue() {
    }


    private static TelnetQueue instance = null;

    public static synchronized TelnetQueue getInstance() {
        if (instance == null) {
            instance = new TelnetQueue();
        }
        return instance;
    }

    public void clean() {
        logger.debug("CommonTelnetClient Queue Destroy ");
        for (LinkedBlockingQueue blockingQueue : telnetQueues.values()) {
            blockingQueue.clear();
            blockingQueue = null;
        }
        telnetQueues = null;
    }

    public void add(String hostname) throws ServiceException {
        this.add(hostname, null, CommonTelnetClient.class);
    }

    public void add(String hostname, Class<AbstractTelnetClient> clazz) throws ServiceException {
        this.add(hostname, null, clazz);
    }

    public void add(TelnetMessage message) throws ServiceException {
        this.add(null, message, null);
    }

    public void add(String hostname, TelnetMessage message) throws ServiceException {
        this.add(hostname, message, CommonTelnetClient.class);
    }

    public void add(String hostname, TelnetMessage message, Class clazz) throws ServiceException {
        this.add(hostname, message, clazz, QUEUE_SIZE);
    }


    public void add(String hostname, TelnetMessage message, Class clazz, Integer initialQueueSize) throws ServiceException {
        try {
            if (hostname != null && telnetQueues.get(hostname) == null) {
                AbstractTelnetClient client = (AbstractTelnetClient) clazz.newInstance();
                logger.debug("CommonTelnetClient Queue add host {}", hostname);
                telnetQueues.put(hostname, new LinkedBlockingQueue(initialQueueSize));
                new Thread(new TelnetConsumer(hostname, telnetQueues.get(hostname), client)).start();
            }
            if (hostname != null && message != null) {
                logger.debug("CommonTelnetClient Queue add Message {}", message);
                telnetQueues.get(hostname).put(message);
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }


}
