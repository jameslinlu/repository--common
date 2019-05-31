package com.commons.flume.sink.elasticsearch.utils;

/**
 * Copyright (C)
 * ParseUtils
 * Author: jameslinlu
 */
public class ParseUtil {

    /**
     * long转ip
     *
     * @return
     */
    public static String bytesToIp(long netseq) {
        byte[] bytes = intToBytes(Integer.reverseBytes((int) netseq));
        return new StringBuffer().append(bytes[0] & 0xFF).append('.').append(bytes[1] & 0xFF).append('.').append(bytes[2] & 0xFF).append('.')
                .append(bytes[3] & 0xFF).toString();
    }

    /**
     * long 转port
     *
     * @return
     */
    public static int bytesToPort(long netseq) {
        byte[] bytes = intToBytes(Integer.reverseBytes((int) netseq));
        return new Integer(new StringBuffer().append(bytes[0] & 0xFF).append(bytes[1] & 0xFF).append(bytes[2] & 0xFF).append(bytes[3] & 0xFF).toString());
    }

    /**
     * 整形转为字节数组
     *
     * @param ipInt
     * @return
     */
    public static byte[] intToBytes(int ipInt) {
        byte[] ipAddr = new byte[4];
        ipAddr[0] = (byte) ((ipInt >>> 24) & 0xFF);
        ipAddr[1] = (byte) ((ipInt >>> 16) & 0xFF);
        ipAddr[2] = (byte) ((ipInt >>> 8) & 0xFF);
        ipAddr[3] = (byte) (ipInt & 0xFF);
        return ipAddr;
    }
}
