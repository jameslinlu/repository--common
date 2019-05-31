package com.commons.metadata.model.validator;

import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.validator.model.ResponseValidatorMessage;
import com.commons.metadata.model.validator.model.ValidatorScriptItem;

import java.util.List;
import java.util.Map;

/**
 * 实现自定义校验的接口
 * 如web层校验
 * CommonWebValidator 以接受request为验证来源，实现类 获取字段由RuleValidator获取rule
 * <p>
 * Copyright (C)
 * IValidator
 * Author: jameslinlu
 */
public interface IValidator {

    /**
     * 初始化校验类
     * 用于填充校验字段
     *
     * @throws ServiceException
     */
    abstract void initialize() throws ServiceException;

    /**
     * 校验多个字段属性
     * 由@see initialize 方法填充的规则校验
     * params: fieldName:value
     */
    void process(Map<String, Object> params) throws ServiceException;

    /**
     * 在process方法抛出异常后获取 消息
     *
     * @return
     * @throws ServiceException
     */
    List<ResponseValidatorMessage> getValidatorMessage() throws ServiceException;

    /**
     * 获取校验脚本
     *
     * @return
     * @throws ServiceException
     */
    Map<String,List<ValidatorScriptItem>> getValidatorScript() throws ServiceException;


}
