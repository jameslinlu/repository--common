package com.commons.common.net.telnet.impl;

import com.commons.common.net.model.Writer;
import com.commons.common.net.telnet.AbstractTelnetClient;
import com.commons.common.net.telnet.TelnetQueue;
import com.commons.common.utils.Threads;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.net.TelnetMessage;

import java.util.LinkedList;

/**
 * Copyright (C)
 * CommonTelnetClient
 * Author: jameslinlu
 */
public class CommonTelnetClient extends AbstractTelnetClient {

    public void login(String username, String password) throws Exception {
        this.getWriter().write(username);
        //用户名通常无需特殊判断
        this.processResult(this.getReader().readUntil(":"), this.getWriter());
        this.getWriter().write(password);
        this.processConnectedResult(this.getReader().readUntil("login", "failed", "]", ">"), this.getWriter());
    }

    public void send(String command) throws ServiceException {
        this.getWriter().write(command.trim());
        this.processResult(this.getReader().readUntil("]", ">", "---- more ----"), writer);
    }

    /**
     * 密码输入后处理返回值 每个设备不同可重写
     *
     * @param result
     * @param writer
     * @throws ServiceException
     */
    public void processConnectedResult(String result, Writer writer) throws ServiceException {
        if (result.toLowerCase().contains("failed")
                || result.toLowerCase().contains("login")
                || result.toLowerCase().contains("error")) {
            throw new ServiceException(ResultCode.ERROR_TELNET_LOGIN);
        }
    }

    /**
     * 处理普通命令结果返回 对应长命令可write输入more或其他读取更多
     *
     * @param result
     * @param writer
     */
    public void processResult(String result, Writer writer) throws ServiceException {
        if (result.toLowerCase().endsWith("---- more ----")) {
            this.send("\040");
        }
        if (result.toLowerCase().contains("unrecognized")) {
            throw new ServiceException(ResultCode.ERROR_TELNET_COMMAND);
        }
    }

    @Override
    public String getReaderContent() {
        return super.getReaderContent().replace("---- More ----", "");
    }

    public static void main(String[] args) throws Exception {
        //测试 定时往队列中增加telnet执行单元
        //队列处理回调 @Subscribe TelnetEvent,  CommonEvent.register(service)
        int i = 0;
        while (true) {
            TelnetMessage message1 = new TelnetMessage();
            message1.setHostname("222.84.157.130");
            message1.setPort(23);
            message1.setUsername("zyuc");
            message1.setPassword("Zyuc(@!)16");
            i++;
            message1.setRouteKey("test" + i);
            LinkedList<String> list = new LinkedList<>();
            list.add("system-view");
            list.add("quit");
//            list.add("dis cur");
//            list.add("quit");
            message1.setCommands(list);
            TelnetQueue.getInstance().add("222.84.157.130", message1);
            i++;
            TelnetMessage message2 = new TelnetMessage();
            message2.setHostname("120.196.166.212");
            message2.setPort(23);
            message2.setUsername("zyuc");
            message2.setPassword("zyuc(@!)16");
            message2.setRouteKey("test" + i);
            LinkedList<String> list1 = new LinkedList<>();
            list1.add("system-view");
            list1.add("quit");
//            list1.add("dis cur");
//            list1.add("quit");
            message2.setCommands(list1);
            TelnetQueue.getInstance().add("120.196.166.212", message2);
            Threads.sleep(2500);

        }


    }


}
