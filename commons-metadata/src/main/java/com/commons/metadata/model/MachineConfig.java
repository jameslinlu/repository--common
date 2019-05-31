package com.commons.metadata.model;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Copyright (C)
 * MachineConfig
 * Author: jameslinlu
 */
public class MachineConfig {

    //域
    private String domain;

    //ip
    private String ip;

    //接口
    private String port;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getIp() {
        if (ip == null) {
            return getLocalIp(null);
        }
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public static String getLocalIp(String defaultIp) {
        try {
            if (defaultIp != null) {
                return defaultIp;
            }
            //确保/etc/hosts 中有对应hostname
            InetAddress ia = InetAddress.getLocalHost();
            return ia.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "0.0.0.0";
        }
    }

}
