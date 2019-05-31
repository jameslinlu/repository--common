package com.commons.cache;

/**
 * Copyright (C)
 * ICommonCacheOp
 * Author: jameslinlu
 */
public interface ICacheManager {
    /**
     * 获取自定义的Cache实例
     *
     * @param prefix 写入key前缀
     * @return
     */
    ICacheOperator getCacheOperator(String prefix);
}
