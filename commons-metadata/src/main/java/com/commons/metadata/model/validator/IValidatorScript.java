package com.commons.metadata.model.validator;

import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.validator.model.ValidatorScriptItem;

/**
 * 生成校验脚本
 * Copyright (C)
 * IValidatorScript
 * Author: jameslinlu
 */
public interface IValidatorScript {
    /**
     * 生成前端校验脚本 ,当一个注解指定了多个校验类 则合并多个校验类的Script
     * 校验应当在服务启动后 扫描全部注解 获取注解实现 有无注解
     *
     * @return 可eval的脚本
     */
    ValidatorScriptItem getValidScript() throws ServiceException;
}
