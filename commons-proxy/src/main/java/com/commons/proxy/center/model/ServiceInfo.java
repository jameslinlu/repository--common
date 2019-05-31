package com.commons.proxy.center.model;


import java.io.Serializable;

/**
 * Copyright (C)
 * InterfaceInfo
 * Author: jameslinlu
 */
public class ServiceInfo implements Serializable {
    public ServiceInfo() {
    }

    public ServiceInfo(String name, String version, String serializer, String transfer, String interfaceName, boolean enable, float timeout, float delay) {
        this.name = name;
        this.version = version;
        this.serializer = serializer;
        this.transfer = transfer;
        this.interfaceName = interfaceName;
        this.enable = enable;
        this.timeout = timeout;
        this.delay = delay;
    }


    /**
     * 平台信息
     */
    private Platform platform;

    /**
     * 类名称
     */
    private String name;
    /**
     * 版本号
     */
    private String version;
    /**
     * 发布类型  由serializer  transfer替代
     */
//    @Deprecated
//    private String type;

    /**
     * 序列化类型
     */
    private String serializer;
    /**
     * 传输类型
     */
    private String transfer;
    /**
     * 接口名称
     */
    private String interfaceName;
    /**
     * 接口超时时间
     */
    private float timeout;
    /**
     * 针对mq延迟消费时间
     */
    private float delay;

    private boolean enable;

    public float getDelay() {
        return delay;
    }

    public void setDelay(float delay) {
        this.delay = delay;
    }

    public float getTimeout() {
        return timeout;
    }

    public void setTimeout(float timeout) {
        this.timeout = timeout;
    }

    public String getSerializer() {
        return serializer;
    }

    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }

    public String getTransfer() {
        return transfer;
    }

    public void setTransfer(String transfer) {
        this.transfer = transfer;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }


    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    @Override
    public String toString() {
        return "ServiceInfo{" +
                "platform=" + platform +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", serializer='" + serializer + '\'' +
                ", transfer='" + transfer + '\'' +
                ", interfaceName='" + interfaceName + '\'' +
                ", enable=" + enable +
                '}';
    }
}
