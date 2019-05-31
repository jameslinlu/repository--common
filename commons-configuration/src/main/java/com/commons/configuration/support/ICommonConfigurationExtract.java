package com.commons.configuration.support;

import com.commons.configuration.model.CommonConfigBundle;
import com.commons.metadata.exception.ServiceException;

import java.util.List;

/**
 * 实现后读取自定义需上传的配置信息
 * Copyright (C)
 * ICommonConfigurationExtract
 * Author: jameslinlu
 */
public interface ICommonConfigurationExtract {

    /**
     * 抽取配置信息
     *
     * @return
     * @throws ServiceException
     */
    List<CommonConfigBundle> processExtract() throws ServiceException;

}
