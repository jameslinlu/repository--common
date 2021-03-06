package com.commons.validator.field;

import com.commons.common.utils.StringUtil;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.validator.model.ResponseValidatorMessage;
import com.commons.metadata.model.validator.model.ValidatorScriptItem;

import java.util.List;
import java.util.Map;

/**
 * Copyright (C)
 * RequireFieldValidator
 * Author: jameslinlu
 */
public class RequireCascadeFieldValidator extends AbstractFieldValidator {

    private String[] cascadeField = null;
    private String lastInvalidField = null;

    /**
     * 校验关联参数
     *
     * @param field        此字段有值
     * @param cascadeField 关联字段校验有值
     */
    public RequireCascadeFieldValidator(String field, String... cascadeField) {
        super(field);
        this.cascadeField = cascadeField;
    }

    @Override
    public boolean isValid(Map parameters) throws ServiceException {
        List<String> values = this.getParameters(parameters);
        if (values.isEmpty()) {
            return false;
        }
        for (String value : values) {
            if (!StringUtil.isEmpty(value)) {
                if (cascadeField != null && cascadeField.length != 0) {
                    for (String cascadeFieldName : cascadeField) {
                        if (parameters.get(cascadeFieldName) == null) {
                            lastInvalidField = cascadeFieldName;
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public String getValidatorMessageCode() {
        return ResultCode.Validator.ERROR_FIELD_REQUIRED;
    }

    @Override
    public ResponseValidatorMessage getValidatorMessage(String code) throws ServiceException {
        ResponseValidatorMessage msg = super.getValidatorMessage(code);
        if (lastInvalidField != null) {
            msg.setField(lastInvalidField);
        }
        return msg;
    }

    @Override
    public ValidatorScriptItem getValidScript() throws ServiceException {
        StringBuffer scriptStr = new StringBuffer();
        scriptStr.append("function(value,options,form){");
        scriptStr.append("    if(!value){ return true; } ");
        scriptStr.append("    var fields=[];");
        for (String field : cascadeField) {
            scriptStr.append("    fields.push('").append(field).append("');");
        }
        scriptStr.append("    if(value){");
        scriptStr.append("        for(var idx in fields){");
        scriptStr.append("            if(!form[fields[idx]]){");
        scriptStr.append("                  throw new Error(\"").append(this.getValidatorMessage().getMessage()).append("\");");
        scriptStr.append("            }");
        scriptStr.append("        }");
        scriptStr.append("    }");
        scriptStr.append("    return true;");
        scriptStr.append("}");
        ValidatorScriptItem script = new ValidatorScriptItem();
        script.setValidator(scriptStr.toString());
        script.setMessage(this.getValidatorMessage().getMessage());
        return script;
    }
}
