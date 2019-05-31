package com.commons.validator.field;

import com.commons.common.utils.ContextMessageUtils;
import com.commons.common.utils.StringUtil;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.validator.IValidatorField;
import com.commons.metadata.model.validator.model.ResponseValidatorMessage;
import com.commons.metadata.model.validator.model.ValidatorScriptItem;

import java.util.List;
import java.util.Map;

/**
 * Copyright (C)
 * RequireFieldValidator
 * Author: jameslinlu
 */
public class RequireFieldValidator extends AbstractFieldValidator {


    public RequireFieldValidator(String field) {
        super(field);
    }

    @Override
    public boolean isValid(Map parameters) throws ServiceException {
        List<String> values = this.getParameters(parameters);
        if (values.isEmpty()) {
            return false;
        }
        for (String value : values) {
            if (StringUtil.isEmpty(value)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getValidatorMessageCode() {
        return ResultCode.Validator.ERROR_FIELD_REQUIRED;
    }

    @Override
    public ValidatorScriptItem getValidScript() throws ServiceException {
        StringBuffer scriptStr = new StringBuffer();
        scriptStr.append("function(value,options,form){");
        scriptStr.append("    if(!!!value){ return false; } ");
        scriptStr.append("    return true; ");
        scriptStr.append("} ");
        ValidatorScriptItem script = new ValidatorScriptItem();
        script.setValidator(scriptStr.toString());
        script.setMessage(this.getValidatorMessage().getMessage());
        return script;
    }
}
