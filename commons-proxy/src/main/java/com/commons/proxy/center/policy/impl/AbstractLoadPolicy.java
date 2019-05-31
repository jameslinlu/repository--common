package com.commons.proxy.center.policy.impl;

import com.commons.proxy.center.model.ServiceInfo;
import com.commons.proxy.center.policy.ILoadPolicy;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Copyright (C)
 * BaseLoadPolicy
 * Author: jameslinlu
 */
public abstract class AbstractLoadPolicy implements ILoadPolicy {
    @Override
    public URL getUrl(List<ServiceInfo> services) {
        ServiceInfo service = analysis(services);
        String urlString = service.getPlatform().getUrl() + service.getName();
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
}
