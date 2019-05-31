package com.commons.validator.field;

import com.alibaba.fastjson.JSON;
import com.commons.common.utils.IPUtil;
import com.commons.common.utils.StringUtil;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.validator.model.ValidatorScriptItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Copyright (C)
 * RequireFieldValidator
 * Author: jameslinlu
 */
public class IPv4FieldValidator extends AbstractPatternFieldValidator {

    public IPv4FieldValidator(String field) {
        super(field);
        this.setValueSeparator(",");
        this.addPattern(Pattern.compile("^(((\\d{1,2})|(1\\d{2})|(2[0-4]\\d)|(25[0-5]))\\.){3}((\\d{1,2})|(1\\d{2})|(2[0-4]\\d)|(25[0-5]))(\\/([1-9]|[1-2][0-9]|[3][0-2]))?$"));
        this.addPattern(Pattern.compile("^((((\\d{1,2})|(1\\d{2})|(2[0-4]\\d)|(25[0-5]))\\.){3}((\\d{1,2})|(1\\d{2})|(2[0-4]\\d)|(25[0-5]))\\-?){1,2}$"));

        //支持类似：192.168.0-10
        this.addPattern(Pattern.compile("^(((\\d{1,2})|(1\\d{2})|(2[0-4]\\d)|(25[0-5]))\\.){3}((\\d{1,2})|(1\\d{2})|(2[0-4]\\d)|(25[0-5]))(\\-(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d))?$"));
    }

    @Override
    public String getValidatorMessageCode() {
        return ResultCode.Validator.ERROR_IPV4_INVALID;
    }


}
