package com.commons.cache.manager;

import com.commons.cache.ICacheManager;

import java.util.Map;

/**
 * Copyright (C)
 * CommonCacheManager
 * Author: jameslinlu
 */
public class CommonCacheManager {
    private ICacheManager defaultManager;//默认
    private Map<String, ICacheManager> managers;//其他 用于集成异构系统缓存

    public void setDefaultManager(ICacheManager defaultManager) {
        this.defaultManager = defaultManager;
    }

    public void setManagers(Map<String, ICacheManager> managers) {
        this.managers = managers;
    }

    public ICacheManager getManager(String key) {
        if (managers == null || !managers.containsKey(key) || key.toLowerCase().equals("default")) {
            return defaultManager;
        }
        return managers.get(key);
    }

}
