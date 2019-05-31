package com.commons.flume.sink.http;

import com.commons.metadata.model.collect.IParse;
import com.google.common.base.Throwables;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.conf.ComponentConfiguration;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Copyright (C)
 * CommonHttpRequestFactory
 * Author: jameslinlu
 */
public class CommonHttpRequestFactory implements HttpRequestFactory {

    private static final Logger logger = LoggerFactory.getLogger(CommonHttpRequestFactory.class);

    private IParse parse;

    @Override
    public HttpRequest createRequest(Event event) {
        Object obj = parse.parseEvent(event);
        if (obj == null) {
            return null;
        }
        HttpRequest request = new HttpRequest();
        if (obj instanceof Map) {
            request.setParams((Map) obj);
        }
        if (obj instanceof AbstractHttpEntity) {
            request.setEntity((AbstractHttpEntity) obj);
        }
        if (obj instanceof String) {
            request.setEntity(new StringEntity((String) obj, ContentType.APPLICATION_JSON));
        }
        logger.debug("CreateRequest Body:{}", obj);
        return request;
    }

    @Override
    public void configure(Context context) {
        logger.debug("context {}", context);
        if (parse == null) {
            String parseClazz = context.getString("parse");
            logger.debug("configure parse class {}", parseClazz);
            try {
                Class<? extends IParse> clazz = (Class<? extends IParse>) Class.forName(parseClazz);
                parse = clazz.newInstance();
                parse.configure(context);
            } catch (ClassNotFoundException e) {
                logger.error("parse class not found error ", e);
                Throwables.propagate(e);
            } catch (InstantiationException e) {
                logger.error("new instance error ", e);
                Throwables.propagate(e);
            } catch (IllegalAccessException e) {
                logger.error("class illegal access error ", e);
                Throwables.propagate(e);
            }
        }
    }

    @Override
    public void configure(ComponentConfiguration componentConfiguration) {

    }
}
