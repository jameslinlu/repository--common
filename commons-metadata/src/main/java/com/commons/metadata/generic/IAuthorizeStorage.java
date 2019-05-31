package com.commons.metadata.generic;

import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.AuthToken;

/**
 * 授权存储数据
 * Copyright (C)
 * IUserStorage
 * Author: jameslinlu
 */
public interface IAuthorizeStorage {

    void put(AuthToken token, Object user) throws ServiceException;

    void remove(String tokenValue) throws ServiceException;

    <T> T get(String tokenValue, Class<T> clazz) throws ServiceException;
}
