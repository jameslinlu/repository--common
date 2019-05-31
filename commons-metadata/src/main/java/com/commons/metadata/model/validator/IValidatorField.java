package com.commons.metadata.model.validator;

import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.validator.model.ResponseValidatorMessage;

import java.util.Map;

/**
 * Copyright (C)
 * IValidatorField
 * Author: jameslinlu
 */
public interface IValidatorField extends IValidatorScript {

    /**
     * 获取校验字段名称
     *
     * @return
     * @throws ServiceException
     */
    String getFieldName() throws ServiceException;

    /**
     * 校验字段值
     *
     * @return
     * @throws ServiceException
     */
    boolean isValid(Map<String, Object> parameters) throws ServiceException;


    /**
     * 校验方法valid返回false时获取错误提示消息
     *
     * @return
     * @throws ServiceException
     */
    ResponseValidatorMessage getValidatorMessage() throws ServiceException;
}
