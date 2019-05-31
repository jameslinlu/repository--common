package com.commons.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * 从上下文获取国际化消息
 * Copyright (C)
 * ContextMessageUtils
 * Author: jameslinlu
 */
public class ContextMessageUtils {

    final static Logger logger = LoggerFactory.getLogger(ContextMessageUtils.class);

    public static String[] getMessages(String code) {
        String message[] = null;
        try {
            message = ContextUtil.getMessage(code).split("\\|");
        } catch (Exception e) {
            logger.error("Context get Message Fail {}",code);
            return new String[]{code,"-1"};
        }
        return message;
    }



}
