package com.commons.metadata.model.validator.model;

import java.io.Serializable;

/**
 * Copyright (C)
 * ResponseValidatorMessage
 * Author: jameslinlu
 */
public class ResponseValidatorMessage implements Serializable {

    /**
     * 字段错误码
     */
    private String code = "";

    /**
     * 字段名称
     */
    private String field = "";

    /**
     * 字段错误描述
     */
    private String message = "";

    public ResponseValidatorMessage(String field, String code, String message) {
        this.code = code;
        this.field = field;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
