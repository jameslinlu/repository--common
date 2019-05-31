package com.commons.common.auth;

import com.commons.common.utils.WebUtil;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.generic.IAuthorizeStorage;
import com.commons.metadata.model.AuthToken;

/**
 * 认证存储到session 不可集群 仅供单机使用
 * Copyright (C)
 * SessionAuthorizeStorage
 * Author: jameslinlu
 */
public class SessionAuthorizeStorage implements IAuthorizeStorage {

    @Override
    public void put(AuthToken token, Object user) throws ServiceException {
        if (user == null) {
            throw new ServiceException(ResultCode.ERROR_PARAMETER_REQUIRED);
        }
        WebUtil.getSession().setAttribute(token.getType() + ":" + token.getValue(), user);
    }

    @Override
    public void remove(String tokenValue) throws ServiceException {
        WebUtil.getSession().removeAttribute(tokenValue);
    }

    @Override
    public <T> T get(String tokenValue, Class<T> clazz) throws ServiceException {
        return WebUtil.getSessionAttribute(tokenValue);
    }
}
