package com.commons.cache.operator;

import com.commons.cache.ICacheOperator;
import com.commons.cache.util.CacheHelper;
import org.springframework.cache.Cache;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (C)
 * RedisCacheOperator
 * Author: jameslinlu
 */
public class RedisCacheOperator implements ICacheOperator {

    private Cache cache;
    private String prefix;

    public RedisCacheOperator(Cache cache, String prefix) {
        this.cache = cache;
        this.prefix = prefix;
    }


    public StringRedisTemplate getNativeCache() {
        return (StringRedisTemplate) cache.getNativeCache();
    }

    /**
     * 按key取string
     *
     * @param key
     * @return
     */
    public String get(String key) {
        return get(key, String.class);
    }

    /**
     * 按key取对象
     */
    public <T> T get(String key, Class<T> clazz) {
        return (T) CacheHelper.deserialize(getNativeCache().opsForValue().get(CacheHelper.getKey(prefix, key)), clazz);
    }

    /**
     * 按key取 List
     */
    public <T> List<T> getList(String key, Class<T> clazz) {
        return (List<T>) CacheHelper.deserialize(getNativeCache().opsForValue().get(CacheHelper.getKey(prefix, key)), clazz);
    }

    /**
     * 按key正则取Map
     */
    public <T> Map<String, T> keys(String patten, Class<T> clazz) {
        Map<String, T> result = null;
        result = new HashMap<>();
        Set<String> keys = getNativeCache().keys(CacheHelper.getKey(prefix, patten));
        for (String key : keys) {
            key = key.replaceAll(CacheHelper.getKey(prefix, ""), "");
            result.put(key, this.get(key, clazz));
        }
        return result;
    }

    @Override
    public Long keysCount(String pattern) {
        Set<String> keys = getNativeCache().keys(CacheHelper.getKey(prefix, pattern));
        return Long.valueOf(keys.size());
    }

    /**
     * 按key set对象
     */
    public void set(String key, Object value) {
        getNativeCache().opsForValue().set(CacheHelper.getKey(prefix, key), CacheHelper.serialize(value));
    }

    /**
     * 按key set对象 and 超时时间
     */
    public void set(String key, Object value, Integer seconds) {
        if (seconds != null && seconds > 0) {
            getNativeCache().opsForValue().set(CacheHelper.getKey(prefix, key), CacheHelper.serialize(value), seconds, TimeUnit.SECONDS);
        } else {
            set(key, value);
        }
    }

    /**
     * 按key设置过期
     */
    public void expire(String key, Integer seconds) {
        if (seconds != null && seconds > 0) {
            getNativeCache().expire(CacheHelper.getKey(prefix, key), seconds, TimeUnit.SECONDS);
        }
    }

    /**
     * 按key删除
     */
    public void del(String key) {
        getNativeCache().delete(CacheHelper.getKey(prefix, key));
    }

    /**
     * 删除所有key
     */
    public void delAll() {
        getNativeCache().delete(getNativeCache().keys(CacheHelper.getKey(prefix, "*")));
    }
}
