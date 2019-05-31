package com.commons.metadata.model.log.enums;

import java.io.Serializable;

/**
 * Copyright (C)
 * CustomerOrderPayEnum
 * Author: jameslinlu
 */
public enum LoggerLevel implements Serializable {

    //5：系统发生了严重的错误, 必须马上进行处理, 否则系统将无法继续运行. 比如, NPE, 数据库不可用等.
    //4：系统能继续运行, 但是必须引起关注. 对于存在的问题一般可以分为两类 一种系统存在明显的问题(比如, 数据不可用), 另一种就是系统存在潜在的问题, 需要引起注意或者给出一些建议(比如, 系统运行在安全模式或者访问当前系统的账号存在安全隐患). 总之就是系统仍然可用, 但是最好进行检查和调整.
    //3：重要的业务逻辑处理完成. 在理想情况下, INFO的日志信息要能让高级用户和系统管理员理解, 并从日志信息中能知道系统当前的运行状态. 比如对于一个机票预订系统来说, 当一个用户完成一个机票预订操作之后, 提醒应该给出"谁预订了从A到B的机票". 另一个需要输出INFO信息的地方就是一个系统操作引起系统的状态发生了重大变化(比如数据库更新, 过多的系统请求).
    //2：主要给开发人员看, 下面会进一步谈到.
    //1：系统详细信息, 主要给开发人员用, 一般来说, 如果是线上系统的话, 可以认为是临时输出, 而且随时可以通过开关将其关闭. 有时候我们很难将DEBUG和TRACE区分开, 一般情况下, 如果是一个已经开发测试完成的系统, 再往系统中添加日志输出, 那么应该设为TRACE级别.

    TRACE(1, "TRACE"),
    DEBUG(2, "DEBUG"),
    INFO(3, "INFO"),
    WARN(4, "WARN"),
    ERROR(5, "ERROR");


    /**
     * 编号
     */
    private Integer code;

    /**
     * 描述
     */
    private String desc;

    LoggerLevel(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取描述
     *
     * @param code
     * @return
     */
    public static LoggerLevel getEnum(Integer code) {
        for (LoggerLevel c : LoggerLevel.values()) {
            if (c.code.intValue() == code.intValue()) {
                return c;
            }
        }
        return null;
    }

    public static LoggerLevel getEnum(String desc) {
        for (LoggerLevel c : LoggerLevel.values()) {
            if (c.desc.equalsIgnoreCase(desc)) {
                return c;
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


    @Override
    public String toString() {
        return desc;
    }
}
