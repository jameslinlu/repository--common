package com.commons.cache.auth;

import com.commons.cache.util.CacheUtil;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.generic.IAuthorizeStorage;
import com.commons.metadata.model.AuthToken;

/**
 * Copyright (C)
 * RedisAuthorizeStorage
 * Author: jameslinlu
 */
public class RedisAuthorizeStorage implements IAuthorizeStorage {
    @Override
    public void put(AuthToken token, Object user) throws ServiceException {
        if (token == null || user == null) {
            throw new ServiceException(ResultCode.ERROR_PARAMETER_REQUIRED);
        }
        CacheUtil.getInstance().getCache().set(token.getType() + ":" + token.getValue(), user, token.getExpire());
    }

    @Override
    public void remove(String tokenValue) throws ServiceException {
        if (tokenValue == null) {
            return;
        }
        CacheUtil.getInstance().getCache().del(tokenValue);
    }

    @Override
    public <T> T get(String tokenValue, Class<T> clazz) throws ServiceException {
        if (tokenValue == null) {
            return null;
        }
        return CacheUtil.getInstance().getCache().get(tokenValue, clazz);
    }
}
