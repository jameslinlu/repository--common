package com.commons.proxy.center.config;

import com.commons.proxy.center.model.Platform;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Copyright (C)
 * ProxyConfig
 * Author: jameslinlu
 */
public class BaseProxyConfig {

    private String ip;
    private String port;
    private String domain;
    private String protocol = "http";
    //默认内存大小MB
    private Double weight = BigDecimal.valueOf(Runtime.getRuntime().totalMemory()).divide(BigDecimal.valueOf(1024 * 1024), 2, BigDecimal.ROUND_HALF_UP).doubleValue();//默认权重

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public String getDomain() {
        return domain;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }


    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Platform getPlatformInfo() {
        Platform platform = new Platform(protocol, getLocalIp(ip), port, domain, weight);
        return platform;
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
