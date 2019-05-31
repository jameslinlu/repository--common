package com.commons.cache;

import java.util.List;
import java.util.Map;

/**
 * Copyright (C)
 * ICacheOperator
 * Author: jameslinlu
 */
public interface ICacheOperator {

    <T> T getNativeCache();

    /**
     * 按key取string
     *
     * @param key
     * @return
     */
    String get(String key);

    /**
     * 按key取对象
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 按key取 List
     */
    <T> List<T> getList(String key, Class<T> clazz);

    /**
     * 按key正则取Map
     */
    <T> Map<String, T> keys(String pattern, Class<T> clazz);

    /**
     * 按key获取对应数量
     *
     * @param pattern
     * @return
     */
    Long keysCount(String pattern);

    /**
     * 按key set对象
     */
    void set(String key, Object value);

    /**
     * 按key set对象 and 超时时间
     */
    void set(String key, Object value, Integer seconds);

    /**
     * 按key设置过期
     */
    void expire(String key, Integer seconds);

    /**
     * 按key删除
     */
    void del(String key);

    /**
     * 删除所有key
     */
    void delAll();
}
