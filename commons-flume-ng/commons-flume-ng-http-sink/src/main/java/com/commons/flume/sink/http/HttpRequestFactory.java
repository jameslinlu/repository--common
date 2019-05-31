package com.commons.flume.sink.http;

import org.apache.flume.Event;
import org.apache.flume.conf.Configurable;
import org.apache.flume.conf.ConfigurableComponent;

/**
 * Copyright (C)
 * HttpRequestFactory
 * Author: jameslinlu
 */
public interface HttpRequestFactory extends Configurable, ConfigurableComponent {
    /**
     * 创建请求 仅包含请求体
     *
     * @param event
     * @return
     */
    HttpRequest createRequest(Event event);
}
