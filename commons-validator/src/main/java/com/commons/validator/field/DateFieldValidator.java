package com.commons.validator.field;

import com.alibaba.fastjson.util.TypeUtils;
import com.commons.common.utils.StringUtil;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.validator.model.ValidatorScriptItem;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C)
 * RequireFieldValidator
 * Author: jameslinlu
 */
public class DateFieldValidator extends AbstractFieldValidator {

    public DateFieldValidator(String field) {
        super(field);
    }

    @Override
    public boolean isValid(Map parameters) throws ServiceException {
        List<String> values = this.getParameters(parameters);
        for (String value : values) {
            if (!StringUtil.isEmpty(value)) {
                try {
                    TypeUtils.castToDate(value);
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String getValidatorMessageCode() {
        return ResultCode.Validator.ERROR_DATE_INVALID;
    }

    @Override
    public ValidatorScriptItem getValidScript() throws ServiceException {
        StringBuffer scriptStr = new StringBuffer();
        scriptStr.append("function(value,options,form){");
        scriptStr.append("    if(!value){ return true; }   ");
        scriptStr.append("    var val = Date.parse(value);");
        scriptStr.append("    var valid=!isNaN(val);");
        scriptStr.append("    if(!valid){");
        scriptStr.append("        throw new Error(\"").append(this.getValidatorMessage().getMessage()).append("\");");
        scriptStr.append("    }");
        scriptStr.append("    return true;");
        scriptStr.append("}");
        ValidatorScriptItem script = new ValidatorScriptItem();
        script.setValidator(scriptStr.toString());
        script.setMessage(this.getValidatorMessage().getMessage());
        return script;
    }
}
