package com.commons.log.utils;

import com.commons.common.utils.Identities;

/**
 * 日志上下文工具
 * Copyright (C)
 * Author: jameslinlu
 */
public class LoggerContextUtil {


    private static final ThreadLocal<String> traceIdThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<String> sequenceIdThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<String> sequenceIdLocalThreadLocal = new ThreadLocal<>();

    /**
     * 获取追踪ID
     *
     * @return
     */
    public static String getTraceId() {
        if (traceIdThreadLocal.get() == null) {
            setTraceId(Identities.getRandomGUID(true));
        }
        return traceIdThreadLocal.get();
    }

    /**
     * 设置追踪ID 在访问入口若有值则记录
     *
     * @param traceId
     */
    public static void setTraceId(String traceId) {
        if (traceId == null || traceId.equals("")) {
            //init
            getTraceId();
            return;
        }
        traceIdThreadLocal.set(traceId);
    }

    /**
     * 获取序号ID
     *
     * @return
     */
    public static String getSequenceId() {
        if (sequenceIdThreadLocal.get() == null) {
            setSequenceId("0");
            sequenceIdLocalThreadLocal.remove();
            getSequenceIdLocal();
        }

        return sequenceIdThreadLocal.get();
    }

    /**
     * 设置序号ID 在访问入口若有值则记录
     *
     * @param sequenceId
     */
    public static void setSequenceId(String sequenceId) {
        sequenceIdThreadLocal.set(sequenceId);
    }

    /**
     * 本地增量序号ID
     *
     * @return
     */
    public static String getSequenceIdLocal() {
        if (sequenceIdLocalThreadLocal.get() == null) {
            setSequenceIdLocal(".-1");
        }

        return sequenceIdLocalThreadLocal.get();
    }

    public static String getSequenceIdLocalAndIncrement() {
        String id = getSequenceIdLocal();
        String prefix = id.substring(0, id.lastIndexOf(".") + 1);
        String lastId = id.substring(id.lastIndexOf(".") + 1, id.length());
        lastId = String.valueOf(Integer.parseInt(lastId) + 1);
        setSequenceIdLocal(prefix + lastId);
        return sequenceIdLocalThreadLocal.get();
    }

    public static void reset() {
        sequenceIdThreadLocal.remove();
        sequenceIdLocalThreadLocal.remove();
        traceIdThreadLocal.remove();
    }

    /**
     * 本地增量序号
     *
     * @param sequenceId
     */
    public static void setSequenceIdLocal(String sequenceId) {
        sequenceIdLocalThreadLocal.set(sequenceId);
    }

}
