package com.commons.configuration.model;

import java.io.Serializable;

/**
 * Copyright (C)
 * CommonConfig
 * Author: jameslinlu
 */
public class CommonConfig implements Serializable {
    //参数名 中文= 默认密码
    private String name;
    //参数key = user.default.password 唯一标示
    private String key;
    // 参数值 = 123456
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
