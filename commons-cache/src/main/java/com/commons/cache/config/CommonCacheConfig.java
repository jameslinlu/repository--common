package com.commons.cache.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * Copyright (C)
 * CommonCacheConfig
 * Author: jameslinlu
 */
public class CommonCacheConfig implements Serializable {
    private String mode;//single | sentinel
    private String host;//127.0.0.1
    private Integer port;//6379
    private String master;//mymaster
    private String sentinel;//192.168.0.198:26379,192.168.0.79:26379,192.168.0.222:26379
    private Integer timeout;//15000
    private Integer database;//0
    private String password;//
    private Integer maxTotal;//100
    private Integer maxIdle;//10
    private Integer minIdle;//1
    private Long maxWaitMillis;//1800000
    private Boolean testOnBorrow;//true
    private Boolean testOnReturn;//true
    private Boolean testWhileIdle;//true


    public RedissonClient buildRedisson() {
        boolean initialized = false;
        Config config = new Config();
        config.setCodec(new StringCodec());
        //支持single 和 sentinel 模式  未支持cluster
        if (mode != null && mode.equals("single")) {
            if (!StringUtils.isEmpty(host) && !StringUtils.isEmpty(port)) {
                config.useSingleServer().setAddress(host + ":" + port);
            }
            if (!StringUtils.isEmpty(password)) {
                config.useSingleServer().setPassword(password);
            }
            if (!StringUtils.isEmpty(database)) {
                config.useSingleServer().setDatabase(database);
            }
            if (!StringUtils.isEmpty(timeout)) {
                config.useSingleServer().setConnectTimeout(timeout);
            }
            if (!StringUtils.isEmpty(minIdle)) {
                config.useSingleServer().setConnectionMinimumIdleSize(minIdle);
            }
            if (!StringUtils.isEmpty(maxTotal)) {
                config.useSingleServer().setConnectionPoolSize(maxTotal);
            }
            initialized = true;
        }
        if (mode != null && mode.equals("sentinel")) {
            config.useSentinelServers().setReadMode(ReadMode.MASTER_SLAVE);
            if (!StringUtils.isEmpty(sentinel)) {
                config.useSentinelServers().addSentinelAddress(sentinel.split(","));
            }
            if (!StringUtils.isEmpty(master)) {
                config.useSentinelServers().setMasterName(master);
            }
            if (!StringUtils.isEmpty(password)) {
                config.useSentinelServers().setPassword(password);
            }
            if (!StringUtils.isEmpty(database)) {
                config.useSentinelServers().setDatabase(database);
            }
            if (!StringUtils.isEmpty(timeout)) {
                config.useSentinelServers().setConnectTimeout(timeout);
            }
            initialized = true;
        }
        if (!initialized) {
            throw new RuntimeException("Cache Initialize Fail ,Cannot Find Attributes");
        }
        return Redisson.create(config);
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getSentinel() {
        return sentinel;
    }

    public void setSentinel(String sentinel) {
        this.sentinel = sentinel;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getDatabase() {
        return database;
    }

    public void setDatabase(Integer database) {
        this.database = database;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public Integer getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public Long getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(Long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public Boolean getTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(Boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public Boolean getTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(Boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public Boolean getTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(Boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }


}
