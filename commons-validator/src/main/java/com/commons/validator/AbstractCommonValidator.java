package com.commons.validator;

import com.commons.common.utils.Base64ParamUtils;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.validator.IValidator;
import com.commons.metadata.model.validator.IValidatorField;
import com.commons.metadata.model.validator.model.ResponseValidatorMessage;
import com.commons.metadata.model.validator.model.ValidatorScriptItem;
import com.commons.validator.support.spring.ValidatorScriptController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 桥接校验
 * 可自定义实现 以便获取的不同的字段来源方式
 * 此类用于从http parameter中获取字段值
 * Copyright (C)
 * AbstractCommonValidator
 * Author: jameslinlu
 */
public abstract class AbstractCommonValidator implements IValidator {

//    自定义实现以填充校验字段
//    public void initialize() throws ServiceException {}

    private static final Logger logger = LoggerFactory.getLogger(ValidatorScriptController.class);

    private List<ResponseValidatorMessage> errorMessages = new LinkedList<>();


    private List<IValidatorField> fields = new ArrayList<>();

    public void add(IValidatorField field) {
        if (field == null) {
            return;
        }
        this.fields.add(field);
    }


    @Override
    public Map<String, List<ValidatorScriptItem>> getValidatorScript() throws ServiceException {
        Map<String, List<ValidatorScriptItem>> scriptMap = new HashMap<>();
        for (IValidatorField field : fields) {
            if (scriptMap.get(field.getFieldName()) == null) {
                scriptMap.put(field.getFieldName(), new LinkedList<ValidatorScriptItem>());
            }
            scriptMap.get(field.getFieldName()).add(field.getValidScript());
        }
        return scriptMap;
    }

    @Override
    public List<ResponseValidatorMessage> getValidatorMessage() {
        try {
            return new LinkedList<>(this.errorMessages);
        } finally {
            this.errorMessages.clear();
        }
    }

    @Override
    public void process(Map<String, Object> params) throws ServiceException {
        for (IValidatorField field : fields) {
            try {
                if (!field.isValid(params)) {
                    errorMessages.add(field.getValidatorMessage());
                    break;
                }
            } catch (ServiceException e) {
                logger.error("AbstractCommonValidator Invoke IsValid Method Fail ", e);
                errorMessages.add(field.getValidatorMessage());
                break;
            }
        }
        if (errorMessages != null && !errorMessages.isEmpty()) {
            throw new ServiceException(ResultCode.ERROR_PARAMETER);
        }
    }
}
