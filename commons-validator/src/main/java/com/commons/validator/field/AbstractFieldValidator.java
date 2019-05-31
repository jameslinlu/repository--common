package com.commons.validator.field;

import com.commons.common.utils.ContextMessageUtils;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.validator.IValidatorField;
import com.commons.metadata.model.validator.model.ResponseValidatorMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C)
 * AbstractFieldValidator
 * Author: jameslinlu
 */
public abstract class AbstractFieldValidator implements IValidatorField {

    private String field;

    public AbstractFieldValidator(String field) {
        this.field = field;
    }

    /**
     * 获取校验字段名称
     *
     * @return
     * @throws ServiceException
     */
    public String getFieldName() throws ServiceException {
        return this.field;
    }

    public abstract String getValidatorMessageCode();

    public String[] getValidatorMessageArguments() {
        return null;
    }

    @Override
    public ResponseValidatorMessage getValidatorMessage() throws ServiceException {
        return this.getValidatorMessage(this.getValidatorMessageCode());
    }

    public ResponseValidatorMessage getValidatorMessage(String code) throws ServiceException {
        String[] msg = ContextMessageUtils.getMessages(code);
        if (this.getValidatorMessageArguments() != null) {
            msg[0] = String.format(msg[0], this.getValidatorMessageArguments());
        }
        return new ResponseValidatorMessage(this.getFieldName(), msg[1], msg[0]);
    }

    public List<String> getParameters(Map parameters) throws ServiceException {
        List<String> results = new ArrayList<>();//若字段未取到值 不认为是校验错误,需增加非空校验
        Object param = parameters.get(this.getFieldName());
        if (param != null && param.getClass().isAssignableFrom(String[].class)) {
            String[] values = (String[]) parameters.get(this.getFieldName());
            results = new ArrayList<>(Arrays.asList(values));
        }
        return results;
    }

}
