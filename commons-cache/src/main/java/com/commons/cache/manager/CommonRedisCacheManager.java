package com.commons.cache.manager;

import com.commons.cache.ICacheManager;
import com.commons.cache.ICacheOperator;
import com.commons.cache.operator.RedisCacheOperator;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisOperations;

/**
 * Copyright (C)
 * CommonRedisCacheManager
 * Author: jameslinlu
 */
public class CommonRedisCacheManager extends RedisCacheManager implements ICacheManager {

    public CommonRedisCacheManager(boolean usePrefix, boolean transactionAware, RedisOperations redisOperations) {
        super(redisOperations);
        this.setUsePrefix(usePrefix);
        //此字段没发现有什么卵用，是通过StringRedisTemplate的supportTransaction来支持事务的
        //需要设置事务时 应该重新定义CacheManager并在xml ref给注解
        this.setTransactionAware(transactionAware);
    }

    @Override
    public ICacheOperator getCacheOperator(String prefix) {
        return new RedisCacheOperator(this.getCache(prefix), prefix);
    }
}
