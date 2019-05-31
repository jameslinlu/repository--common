package com.commons.cache.operator;

import com.commons.cache.ICacheOperator;
import com.commons.cache.util.CacheHelper;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.RedissonCache;
import org.springframework.cache.Cache;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (C)
 * RedisCacheOperator
 * Author: jameslinlu
 */
public class RedissonCacheOperator implements ICacheOperator {

    private Cache cache;
    private String prefix;

    public RedissonCacheOperator(Cache cache, String prefix) {
        this.cache = cache;
        this.prefix = prefix;
    }


    public RedissonClient getNativeCache() {
        Field field = ReflectionUtils.findField(RedissonCache.class, "redisson");
        ReflectionUtils.makeAccessible(field);
        RedissonClient redisson = (RedissonClient) ReflectionUtils.getField(field, cache);
        return redisson;
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
        RBucket<String> bucket = getNativeCache().getBucket(CacheHelper.getKey(prefix, key));
        return (T) CacheHelper.deserialize(bucket.get(), clazz);
    }

    /**
     * 按key取 List
     */
    public <T> List<T> getList(String key, Class<T> clazz) {
        return (List<T>) get(key, clazz);
    }

    /**
     * 按key正则取Map
     */
    public <T> Map<String, T> keys(String patten, Class<T> clazz) {
        Map<String, T> result = new HashMap<>();
        Set<String> keys = new HashSet<>(getNativeCache().getKeys().findKeysByPattern(CacheHelper.getKey(prefix, patten)));
        for (String key : keys) {
            key = key.replaceAll(CacheHelper.getKey(prefix, ""), "");
            result.put(key, this.get(key, clazz));
        }
        return result;
    }

    @Override
    public Long keysCount(String pattern) {
        return getNativeCache().getKeys().countExists(CacheHelper.getKey(prefix, pattern));
    }

    /**
     * 按key set对象
     */
    public void set(String key, Object value) {
        getNativeCache().getBucket(CacheHelper.getKey(prefix, key)).set(CacheHelper.serialize(value));
    }

    /**
     * 按key set对象 and 超时时间
     */
    public void set(String key, Object value, Integer seconds) {
        if (seconds != null && seconds > 0) {
            getNativeCache().getBucket(CacheHelper.getKey(prefix, key)).set(CacheHelper.serialize(value), seconds, TimeUnit.SECONDS);
        } else {
            set(key, value);
        }
    }

    /**
     * 按key设置过期
     */
    public void expire(String key, Integer seconds) {
        if (seconds != null && seconds > 0) {
            getNativeCache().getBucket(key).expire(seconds, TimeUnit.SECONDS);
        }
    }

    /**
     * 按key删除
     */
    public void del(String key) {
        getNativeCache().getBucket(CacheHelper.getKey(prefix, key)).delete();
    }

    /**
     * 删除所有key
     */
    public void delAll() {
        getNativeCache().getKeys().deleteByPattern(CacheHelper.getKey(prefix, "*"));
    }
}
