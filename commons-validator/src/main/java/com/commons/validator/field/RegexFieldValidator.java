package com.commons.validator.field;

import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.validator.model.ValidatorScriptItem;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Copyright (C)
 * RegexFieldValidator
 * Author: jameslinlu
 */
public class RegexFieldValidator extends AbstractPatternFieldValidator {

    private String code;
    private String[] messages;//code对应的message中的占位替换内容

    public RegexFieldValidator(String field, List<Pattern> patterns, String code, String[] messages) {
        super(field, patterns);
        this.code = code;
        this.messages = messages;
    }

    @Override
    public String getValidatorMessageCode() {
        return code;
    }

    @Override
    public String[] getValidatorMessageArguments() {
        return messages;
    }

}
