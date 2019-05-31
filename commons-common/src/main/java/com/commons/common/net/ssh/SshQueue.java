package com.commons.common.net.ssh;

import com.commons.common.net.ssh.impl.CommonSshClient;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.net.SshMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Ssh业务的Producer 单例执行 每个IP一个队列
 * 队列处理回调@Subscribe SshEvent, CommonEvent.register(service)
 * Copyright (C)
 * SshQueue
 * Author: jameslinlu
 */
public class SshQueue {

    private static final Logger logger = LoggerFactory.getLogger(SshQueue.class);

    public Map<String, LinkedBlockingQueue> sshQueues = new ConcurrentHashMap<>();
    private static final Integer QUEUE_SIZE = 5;


    private SshQueue() {
    }

    private static SshQueue instance = null;


    public static synchronized SshQueue getInstance() {
        if (instance == null) {
            instance = new SshQueue();
        }
        return instance;
    }

    public void clean() {
        logger.debug("CommonSshClient Queue Destroy ");
        for (LinkedBlockingQueue blockingQueue : sshQueues.values()) {
            blockingQueue.clear();
            blockingQueue = null;
        }
        sshQueues = null;
    }

    public void add(String hostname) throws ServiceException {
        this.add(hostname, null, CommonSshClient.class);
    }

    public void add(String hostname, Class<AbstractSshClient> clazz) throws ServiceException {
        this.add(hostname, null, clazz);
    }

    public void add(SshMessage message) throws ServiceException {
        this.add(null, message, null);
    }

    public void add(String hostname, SshMessage message) throws ServiceException {
        this.add(hostname, message, CommonSshClient.class);
    }

    public void add(String hostname, SshMessage message, Class clazz) throws ServiceException {
        this.add(hostname, message, clazz, QUEUE_SIZE);
    }

    public void add(String hostname, SshMessage message, Class clazz, Integer initialQueueSize) throws ServiceException {
        try {
            if (hostname != null && sshQueues.get(hostname) == null) {
                AbstractSshClient client = (AbstractSshClient) clazz.newInstance();
                logger.debug("CommonSshClient Queue add host {}", hostname);
                sshQueues.put(hostname, new LinkedBlockingQueue(initialQueueSize));
                new Thread(new SshConsumer(hostname, sshQueues.get(hostname), client)).start();
            }
            if (hostname != null && message != null) {
                logger.debug("CommonSshClient Queue add Message {}", message);
                sshQueues.get(hostname).put(message);
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }


}
