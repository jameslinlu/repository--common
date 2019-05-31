package com.commons.log.parser;

import com.alibaba.fastjson.JSON;
import com.commons.metadata.model.collect.IParse;
import org.apache.commons.lang3.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 解析日志文本转换对象
 * Copyright (C)
 * LogTextParser
 * Author: jameslinlu
 */
public class LogTextParser implements IParse {

    private static final Logger logger = LoggerFactory.getLogger(LogTextParser.class);

    @Override
    public Object parseEvent(Event event) {
        byte[] bodies = event.getBody();
        if (bodies == null || bodies.length <= 0) {
            return null;
        }
        String body = new String(bodies);
        if (StringUtils.isEmpty(body)) {
            return null;
        }
        try {
            return JSON.parseObject(body);
        } catch (Exception e) {
            logger.error("parse log text fail {}", body, e);
            return null;
        }
    }

    @Override
    public void configure(Context context) {
        //nothing,  or config domain?  no,use index_type
    }
}
