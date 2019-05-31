package com.commons.flume.sink.elasticsearch.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.util.Pool;

import java.util.*;

/**
 * Copyright (C)
 * RedisUtil
 * Author: jameslinlu
 */
public class JedisHelper {

    private static final Logger logger = LoggerFactory.getLogger(JedisHelper.class);

    private static Map<String, JedisHelper> helper = new HashMap<>();

    public static final String REDIS_KEY = "redisKey";
    public static final String REDIS_HOST = "redisHost";
    public static final String REDIS_PORT = "redisPort";
    public static final String REDIS_MASTER_NAME = "redisMasterName";
    public static final String REDIS_SENTINELS = "redisSentinels";
    public static final String REDIS_KEY_PREFIX = "redisKeyPrefix";

    private Pool<Jedis> pool;
    private String redisKeyPrefix;


    private JedisHelper() {
    }

    public static JedisHelper getInstance(String key) {
        if (helper.get(key) == null) {
            //support same jvm diff client
            helper.put(key, new JedisHelper());
        }
        return helper.get(key);
    }

    public Jedis getResource() {
        try {
            return pool.getResource();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getKeyPrefix() {
        return redisKeyPrefix;
    }

    public synchronized void configure(Context context) {
        if (pool != null) {
            return;
        }
        logger.debug(" configure in {}", context.toString());
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(100);
        poolConfig.setMaxIdle(20);
        poolConfig.setMinIdle(5);
        poolConfig.setMaxWaitMillis(10l * 1000l);
        poolConfig.setTestOnBorrow(true);

        redisKeyPrefix = context.getString(REDIS_KEY_PREFIX, "");
        logger.debug("redis config {} {}", REDIS_KEY_PREFIX, redisKeyPrefix);
        String masterName = context.getString(REDIS_MASTER_NAME);
        if (StringUtils.isEmpty(masterName)) {
            String host = context.getString(REDIS_HOST);
            Integer port = context.getInteger(REDIS_PORT);
            logger.debug("redis config {} {} ", REDIS_HOST, host);
            logger.debug("redis config {} {} ", REDIS_PORT, port);
            pool = new JedisPool(poolConfig, host, port);
        } else {
            logger.debug("redis config {} {}", REDIS_MASTER_NAME, masterName);
            Set<String> sentinels = new HashSet<>(Arrays.asList(context.getString(REDIS_SENTINELS).split(",")));
            logger.debug("redis config {} {}", REDIS_SENTINELS, sentinels);
            pool = new JedisSentinelPool(masterName, sentinels, poolConfig);
        }
    }

    public JedisHelper debug() {
        pool = new JedisPool("192.168.112.171", 6379);
        redisKeyPrefix = "Default:";
        return this;
    }


}
