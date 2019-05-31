package com.commons.common.events;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Copyright (C)
 * CommonEvent
 * Author: jameslinlu
 */
public class CommonEvent {

    private static final Logger logger = LoggerFactory.getLogger(CommonEvent.class);
    private static final AsyncEventBus events = new AsyncEventBus(Executors.newCachedThreadPool());
    private static List<String> eventHandlerNames = new ArrayList<>();

    static {
        events.register(new Object() {
            @Subscribe
            public void deadEventListener(DeadEvent event) {
                logger.debug("Event Dead [{}]", event.getEvent());
            }
        });
    }

    /**
     * 发送消息
     *
     * @param event
     */
    public static void publish(Object event) {
        logger.debug("Event publish {}", event);
        events.post(event);
    }

    /**
     * 注册处理类 @Subscribe
     *
     * @param service
     */
    public static void register(Object service) {
        String name = service.getClass().getName();
        if (!eventHandlerNames.contains(name)) {
            eventHandlerNames.add(name);
            events.register(service);
        }
    }

    /**
     * 取消注册处理
     *
     * @param service
     */
    public static void unregister(Object service) {
        String name = service.getClass().getName();
        eventHandlerNames.remove(name);
        events.unregister(service);
    }
}
