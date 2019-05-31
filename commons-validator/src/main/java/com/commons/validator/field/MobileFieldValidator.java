package com.commons.validator.field;

import com.commons.common.utils.ContextMessageUtils;
import com.commons.common.utils.StringUtil;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.validator.IValidatorField;
import com.commons.metadata.model.validator.model.ResponseValidatorMessage;
import com.commons.metadata.model.validator.model.ValidatorScriptItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright (C)
 * RequireFieldValidator
 * Author: jameslinlu
 */
public class MobileFieldValidator extends AbstractPatternFieldValidator {

    public MobileFieldValidator(String field) {
        super(field);
        this.addPattern(Pattern.compile("^1[0-9]{10}$"));
    }

    @Override
    public String getValidatorMessageCode() {
        return ResultCode.Validator.ERROR_MOBILE_INVALID;
    }

//    @Override
//    public ValidatorScriptItem getValidScript() throws ServiceException {
//        ValidatorScriptItem script = new ValidatorScriptItem();
//        script.setValidator("isMobilePhone");
//        script.setMessage(this.getValidatorMessage().getMessage());
//        script.setOptions(new String[]{"zh-CN"});
//        return script;
//    }

}
