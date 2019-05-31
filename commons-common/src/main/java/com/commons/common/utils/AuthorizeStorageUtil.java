package com.commons.common.utils;

import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.generic.IAuthorizeStorage;
import com.commons.metadata.model.AuthToken;

/**
 * 授权存储工具类
 * Copyright (C)
 * AuthorizeStorageUtil
 * Author: jameslinlu
 */
public class AuthorizeStorageUtil {

    private static IAuthorizeStorage storage;

    private static void initialize() {
        if (storage == null) {
            storage = ContextUtil.getBean(IAuthorizeStorage.class);
        }
    }

    public static void put(AuthToken token, Object user) throws ServiceException {
        initialize();
        storage.put(token, user);
    }

    public static void remove(String tokenValue) throws ServiceException {
        initialize();
        storage.remove(tokenValue);
    }

    public static <T> T get(String tokenValue, Class<T> clazz) throws ServiceException {
        initialize();
        return storage.get(tokenValue, clazz);
    }
}
