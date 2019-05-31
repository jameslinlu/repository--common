package com.commons.metadata.model.validator.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Copyright (C)
 * ValidatorScriptItem
 * Author: jameslinlu
 */
public class ValidatorScriptItem implements Serializable {

    private String validator;//校验方法字符串
    private Object options;//校验扩展参数
    private String message;//默认校验提示

    public String getValidator() {
        return validator;
    }

    public void setValidator(String validator) {
        this.validator = validator;
    }

    public Object getOptions() {
        return options;
    }

    public void setOptions(Object options) {
        this.options = options;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
