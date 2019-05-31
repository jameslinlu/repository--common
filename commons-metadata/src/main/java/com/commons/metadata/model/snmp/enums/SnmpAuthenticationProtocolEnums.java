package com.commons.metadata.model.snmp.enums;

/**
 * snmp版本
 * Copyright (C)
 */
public enum SnmpAuthenticationProtocolEnums {

    MD5(1, "MD5"),
    SHA(2, "SHA"),
    HMAC128SHA224(3, "HMAC128SHA224"),
    HMAC192SHA256(4, "HMAC192SHA256"),
    HMAC256SHA384(5, "HMAC256SHA384"),
    HMAC384SHA512(6, "HMAC384SHA512");

    private Integer code;

    private String desc;

    SnmpAuthenticationProtocolEnums(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SnmpAuthenticationProtocolEnums getEnum(String desc) {
        for (SnmpAuthenticationProtocolEnums c : SnmpAuthenticationProtocolEnums.values()) {
            if (c.desc.equals(desc)) {
                return c;
            }
        }
        return null;
    }

    public static SnmpAuthenticationProtocolEnums getEnum(Integer code) {
        for (SnmpAuthenticationProtocolEnums c : SnmpAuthenticationProtocolEnums.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return null;
    }

    public static String getName(Integer code) {
        for (SnmpAuthenticationProtocolEnums c : SnmpAuthenticationProtocolEnums.values()) {
            if (c.code.intValue() == code.intValue()) {
                return c.desc;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
