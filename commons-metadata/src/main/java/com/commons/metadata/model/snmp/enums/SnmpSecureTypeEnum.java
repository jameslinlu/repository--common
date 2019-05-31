package com.commons.metadata.model.snmp.enums;

/**
 * snmp版本
 * Copyright (C)
 */
public enum SnmpSecureTypeEnum {
    //code符合snmp4j枚举值 noAuthNoPriv(1), authNoPriv(2), authPriv(3);
    NO_AUTH_NO_PRIV(1, "NO_AUTH_NO_PRIV"),
    AUTH_NO_PRIV(2, "AUTH_NO_PRIV"),
    AUTH_PRIV(3, "AUTH_PRIV");

    private Integer code;

    private String desc;

    SnmpSecureTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SnmpSecureTypeEnum getEnum(String desc) {
        for (SnmpSecureTypeEnum c : SnmpSecureTypeEnum.values()) {
            if (c.desc.equals(desc)) {
                return c;
            }
        }
        return null;
    }

    public static SnmpSecureTypeEnum getEnum(Integer code) {
        for (SnmpSecureTypeEnum c : SnmpSecureTypeEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return null;
    }

    public static String getName(Integer code) {
        for (SnmpSecureTypeEnum c : SnmpSecureTypeEnum.values()) {
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
