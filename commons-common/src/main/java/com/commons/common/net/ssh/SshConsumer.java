package com.commons.common.net.ssh;

import com.commons.common.events.CommonEvent;
import com.commons.metadata.model.net.SshEvent;
import com.commons.metadata.model.net.SshMessage;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Telnet业务的Consumer
 * Copyright (C)
 * TelnetConsumer
 * Author: jameslinlu
 */
public class SshConsumer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SshConsumer.class);

    private LinkedBlockingQueue<SshMessage> queue = null;
    private String consumerKey;
    private AbstractSshClient client;

    public SshConsumer(String consumerKey, LinkedBlockingQueue queue, AbstractSshClient client) {
        this.queue = queue;
        this.consumerKey = consumerKey;
        this.client = client;
    }

    @Override
    public void run() {
        logger.debug("Start CommonSshClient Consumer {}", Thread.currentThread());
        consume();
    }

    private void consume() {
        while (true) {
            SshMessage message = null;
            AbstractSshClient ssh = null;
            try {
                if (queue == null) {
                    logger.error("CommonSshClient Consumer Exit");
                    break;
                }
                message = queue.take();
                Long begin = System.currentTimeMillis();
                logger.debug("Consumer [{}] Take , Left Size {}", consumerKey, queue.size());
                String content = null;
                try {
                    ssh = this.client;
                    ssh.initialize();
                    ssh.connect(message.getHostname(), message.getPort());
                    ssh.login(message.getUsername(), message.getPassword());
                    for (String cmd : message.getCommands()) {
                        ssh.send(cmd);
                    }
                    content = ssh.getReaderContent();
                } catch (Exception e) {
                    logger.error("CommonSshClient Execute Error", e);
                    message.setException(e);
                }
                logger.trace("CommonSshClient Result {}", content);
                message.setContent(content);
                logger.debug("Consumer [{}] End Spend {} ms", consumerKey, System.currentTimeMillis() - begin);
            } catch (Exception e) {
                logger.error("CommonSshClient Consume Exception", e);
            } finally {
                try {
                    if (ssh != null) {
                        ssh.disconnect();
                    }
                    SshEvent event = new SshEvent();
                    BeanUtils.copyProperties(event, message);
                    CommonEvent.publish(event);
                } catch (Exception e) {
                    logger.error("Ssh Consumer Finish Publish Event Fail", e);
                }
            }

        }
    }
}
