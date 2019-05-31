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
public class FloatFieldValidator extends AbstractFieldValidator {


    private String patternFloat = "^(?:[-+])?(?:[0-9]+)?(?:\\.[0-9]*)?(?:[eE][\\+\\-]?(?:[0-9]+))?$";
    private Float min;
    private Float max;

    public FloatFieldValidator(String field) {
        super(field);
    }

    public FloatFieldValidator(String field, Float min, Float max) {
        super(field);
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean isValid(Map parameters) throws ServiceException {
        List<String> values = this.getParameters(parameters);
        for (String value : values) {
            if (!StringUtil.isEmpty(value)) {
                Float val = null;
                try {
                    val = Float.parseFloat(value);
                } catch (NumberFormatException e) {
                    return false;
                }
                if (this.min != null && val < this.min) {
                    return false;
                }
                if (this.max != null && val > this.max) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String getValidatorMessageCode() {
        return ResultCode.Validator.ERROR_FLOAT_INVALID;
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
        scriptStr.append("    var regex=new RegExp(").append(patternFloat).append("); ");
        scriptStr.append("    var valid = regex.test(value) &&");
        scriptStr.append("         (!options.hasOwnProperty('min') || value >= options.min) &&");
        scriptStr.append("         (!options.hasOwnProperty('max') || value <= options.max);");
        scriptStr.append("    if(!valid){");
        scriptStr.append("        throw new Error(\"").append(this.getValidatorMessage().getMessage()).append("\");");
        scriptStr.append("    }");
        scriptStr.append("    return true;");
        scriptStr.append(" } ");

        Map<String, Float> options = new HashMap<>();
        if (min != null) {
            options.put("min", min);
        }
        if (max != null) {
            options.put("max", max);
        }

        ValidatorScriptItem script = new ValidatorScriptItem();
        script.setValidator(scriptStr.toString());
        script.setMessage(this.getValidatorMessage().getMessage());
        script.setOptions(options);
        return script;
    }
}
