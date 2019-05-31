package com.commons.common.net.telnet;

import com.commons.common.events.CommonEvent;
import com.commons.metadata.model.net.TelnetEvent;
import com.commons.metadata.model.net.TelnetMessage;
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
public class TelnetConsumer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TelnetConsumer.class);

    private LinkedBlockingQueue<TelnetMessage> queue = null;
    private String consumerKey;
    private AbstractTelnetClient client;

    public TelnetConsumer(String consumerKey, LinkedBlockingQueue queue, AbstractTelnetClient client) {
        this.queue = queue;
        this.consumerKey = consumerKey;
        this.client = client;
    }

    @Override
    public void run() {
        logger.debug("Start CommonTelnetClient Consumer {}", Thread.currentThread());
        consume();
    }

    private void consume() {
        while (true) {
            TelnetMessage message = null;
            AbstractTelnetClient telnet = null;
            try {
                if (queue == null) {
                    logger.error("CommonTelnetClient Consumer Exit");
                    break;
                }
                message = queue.take();
                Long begin = System.currentTimeMillis();
                logger.debug("Consumer [{}] Take , Left Size {}", consumerKey, queue.size());
                String content = null;
                try {
                    telnet = this.client;
                    telnet.initialize();
                    telnet.connect(message.getHostname(), message.getPort());
                    telnet.login(message.getUsername(), message.getPassword());
                    for (String cmd : message.getCommands()) {
                        telnet.send(cmd);
                    }
                    content = telnet.getReaderContent();
                } catch (Exception e) {
                    logger.error("CommonTelnetClient Execute Error", e);
                    message.setException(e);
                }
                logger.trace("CommonTelnetClient Result {}", content);
                message.setContent(content);
                logger.debug("Consumer [{}] End Spend {} ms", consumerKey, System.currentTimeMillis() - begin);
            } catch (Exception e) {
                logger.error("CommonTelnetClient Consume Exception", e);
            } finally {
                try {
                    if (null != telnet) {
                        telnet.disconnect();
                    }
                    TelnetEvent event = new TelnetEvent();
                    BeanUtils.copyProperties(event, message);
                    CommonEvent.publish(event);
                } catch (Exception e) {
                    logger.error("Telnet Consumer Finish Publish Event Fail", e);
                }
            }

        }
    }
}
