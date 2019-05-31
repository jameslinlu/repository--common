package com.commons.validator.field;

import com.commons.common.utils.StringUtil;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.validator.model.ValidatorScriptItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Copyright (C)
 * RequireFieldValidator
 * Author: jameslinlu
 */
public class EmailFieldValidator extends AbstractPatternFieldValidator {

    public EmailFieldValidator(String field) {
        super(field);
        this.addPattern(Pattern.compile("^[\\.a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$"));
    }

    @Override
    public String getValidatorMessageCode() {
        return ResultCode.Validator.ERROR_EMAIL_INVALID;
    }

//    @Override
//    public ValidatorScriptItem getValidScript() throws ServiceException {
//        ValidatorScriptItem script = new ValidatorScriptItem();
//        script.setValidator("isEmail");
//        script.setMessage(this.getValidatorMessage().getMessage());
//        return script;
//    }
}
