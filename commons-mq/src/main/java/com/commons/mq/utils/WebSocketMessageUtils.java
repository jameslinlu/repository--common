package com.commons.mq.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;

import javax.annotation.PostConstruct;

public class WebSocketMessageUtils extends ApplicationObjectSupport {


    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private SimpUserRegistry simpUserRegistry;

    /**
     * 静态变量
     */
    private static WebSocketMessageUtils holder;

    /**
     * 初始化从spring获取bean
     */
    @PostConstruct
    private void initialize() {
        holder = getApplicationContext().getBean(WebSocketMessageUtils.class);
    }

    /**
     * 支持无法注入处手动调用
     */
    public static WebSocketMessageUtils getInstance() {
        return holder;
    }


    public void send(String username, String destination, Object payload) {
        if (simpUserRegistry.getUser(username) != null) {
            simpMessagingTemplate.convertAndSendToUser(username, destination, payload);
        }
    }

    public void send(String destination, Object payload) {
        //建议发送前校验
        simpMessagingTemplate.convertAndSend(destination, payload);
    }

}