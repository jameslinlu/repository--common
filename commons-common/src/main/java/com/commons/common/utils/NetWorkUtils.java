package com.commons.common.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Copyright (C)
 * NetWorkUtils
 * Author: jameslinlu
 */
public class NetWorkUtils {

    public static void main(String[] args) {
        System.out.println(isHostReachable("192.168.130.5",3000));
    }

    public static boolean isHostConnectable(String host, int port) {
        //判断ip、端口是否可连接
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static boolean isHostReachable(String host, Integer timeOut) {
        //判断ip是否可以连接 timeOut是超时时间
        try {
            return InetAddress.getByName(host).isReachable(timeOut);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
