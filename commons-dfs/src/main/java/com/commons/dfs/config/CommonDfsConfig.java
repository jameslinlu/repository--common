package com.commons.dfs.config;

import java.io.Serializable;
import java.util.Map;

/**
 * Copyright (C)
 * AbstractDfsConfig
 * Author: jameslinlu
 */
public class CommonDfsConfig implements Serializable {
    private String name;
    private Map<String, Object> properties;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
