package com.commons.cache.manager;

import com.commons.cache.ICacheManager;
import com.commons.cache.ICacheOperator;
import com.commons.cache.config.CommonCacheConfig;
import com.commons.cache.operator.RedisCacheOperator;
import com.commons.cache.operator.RedissonCacheOperator;
import org.redisson.api.RedissonClient;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisOperations;

/**
 * Copyright (C)
 * CommonRedisCacheManager
 * Author: jameslinlu
 */
public class CommonRedissonCacheManager extends RedissonSpringCacheManager implements ICacheManager {

    public CommonRedissonCacheManager(CommonCacheConfig cacheConfig) {
        super(cacheConfig.buildRedisson());
    }

    @Override
    public ICacheOperator getCacheOperator(String prefix) {
        return new RedissonCacheOperator(this.getCache(prefix), prefix);
    }
}
