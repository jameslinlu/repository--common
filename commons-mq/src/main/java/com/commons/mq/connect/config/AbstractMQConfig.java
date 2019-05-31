package com.commons.mq.connect.config;

import java.io.Serializable;

/**
 * 抽象mq配置文件
 */
public abstract class AbstractMQConfig implements Serializable {
    private String addresses;
    private String username;
    private String password;

    public String getAddresses() {
        return addresses;
    }

    public void setAddresses(String addresses) {
        this.addresses = addresses;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
