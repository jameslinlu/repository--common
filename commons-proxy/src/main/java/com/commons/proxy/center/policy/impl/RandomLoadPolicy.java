package com.commons.proxy.center.policy.impl;

import com.commons.proxy.center.model.ServiceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

/**
 * 随机加载策略
 * Random随机
 * Copyright (C)
 * RandomPolicy
 * Author: jameslinlu
 */
public class RandomLoadPolicy extends AbstractLoadPolicy {
    final static Logger logger = LoggerFactory.getLogger(RandomLoadPolicy.class);

    Random random = null;

    public RandomLoadPolicy() {
        this.random = new Random();
    }

    @Override
    public ServiceInfo analysis(List<ServiceInfo> services) {
        ServiceInfo service = services.get(random.nextInt(services.size()));
        logger.debug("analysis RandomLoadPolicy service find {}", service.toString());
        return service;
    }
}
