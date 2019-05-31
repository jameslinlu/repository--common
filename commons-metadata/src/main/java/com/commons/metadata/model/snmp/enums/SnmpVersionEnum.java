package com.commons.metadata.model.snmp.enums;

/**
 * snmp版本
 * Copyright (C)
 */
public enum SnmpVersionEnum {

    V2C(1, "V2C"),
    V3(3, "V3");

    private Integer code;

    private String desc;

    SnmpVersionEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SnmpVersionEnum getEnum(String desc) {
        for (SnmpVersionEnum c : SnmpVersionEnum.values()) {
            if (c.desc.equals(desc)) {
                return c;
            }
        }
        return null;
    }

    public static SnmpVersionEnum getEnum(Integer code) {
        for (SnmpVersionEnum c : SnmpVersionEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return null;
    }

    public static String getName(Integer code) {
        for (SnmpVersionEnum c : SnmpVersionEnum.values()) {
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
