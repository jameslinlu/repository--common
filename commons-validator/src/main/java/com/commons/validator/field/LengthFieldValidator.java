package com.commons.validator.field;

import com.commons.common.utils.StringUtil;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.validator.model.ValidatorScriptItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C)
 * RequireFieldValidator
 * Author: jameslinlu

 */
public class LengthFieldValidator extends AbstractFieldValidator {

    private Integer min;
    private Integer max;

    public LengthFieldValidator(String field) {
        super(field);
    }

    public LengthFieldValidator(String field, Integer min, Integer max) {
        super(field);
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean isValid(Map parameters) throws ServiceException {
        List<String> values = this.getParameters(parameters);
        for (String value : values) {
            if (!StringUtil.isEmpty(value)) {
                if (this.min != null && value.length() < this.min) {
                    return false;
                }
                if (this.max != null && value.length() > this.max) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String getValidatorMessageCode() {
        return ResultCode.Validator.ERROR_LENGTH_INVALID;
    }

    @Override
    public String[] getValidatorMessageArguments() {
        return new String[]{String.valueOf(min), String.valueOf(max)};
    }

    @Override
    public ValidatorScriptItem getValidScript() throws ServiceException {

        StringBuffer scriptStr = new StringBuffer();
        scriptStr.append("function(value,options,form){");
        scriptStr.append("    if(!value){ return true; }   ");
        scriptStr.append("    if( (options.min && value.length < options.min) || (options.max && value.length > options.max) ){");
        scriptStr.append("        throw new Error(\"").append(this.getValidatorMessage().getMessage()).append("\");");
        scriptStr.append("    }");
        scriptStr.append("    return true;");
        scriptStr.append(" } ");

        ValidatorScriptItem script = new ValidatorScriptItem();
        script.setValidator(scriptStr.toString());
        script.setMessage(this.getValidatorMessage().getMessage());
        Map<String, Integer> options = new HashMap<>();
        if (min != null) {
            options.put("min", min);
        }
        if (max != null) {
            options.put("max", max);
        }
        script.setOptions(options);
        return script;
    }
}
