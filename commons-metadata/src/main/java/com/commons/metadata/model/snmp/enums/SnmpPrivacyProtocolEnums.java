package com.commons.metadata.model.snmp.enums;

/**
 * snmp版本
 * Copyright (C)
 */
public enum SnmpPrivacyProtocolEnums {

    DES(1, "DES"),
    DES3(2, "DES3"),
    AES128(3, "AES128"),
    AES192(4, "AES192"),
    AES256(5, "AES256");

    private Integer code;

    private String desc;

    SnmpPrivacyProtocolEnums(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SnmpPrivacyProtocolEnums getEnum(String desc) {
        for (SnmpPrivacyProtocolEnums c : SnmpPrivacyProtocolEnums.values()) {
            if (c.desc.equals(desc)) {
                return c;
            }
        }
        return null;
    }

    public static SnmpPrivacyProtocolEnums getEnum(Integer code) {
        for (SnmpPrivacyProtocolEnums c : SnmpPrivacyProtocolEnums.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return null;
    }

    public static String getName(Integer code) {
        for (SnmpPrivacyProtocolEnums c : SnmpPrivacyProtocolEnums.values()) {
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
