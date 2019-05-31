package com.commons.common.net.ssh.impl;

import com.commons.common.net.model.Writer;
import com.commons.common.net.ssh.AbstractSshClient;
import com.commons.common.net.ssh.SshQueue;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.net.SshMessage;
import net.schmizz.sshj.connection.channel.direct.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

/**
 * Copyright (C)
 * CommonTelnetClient
 * Author: jameslinlu
 */
public class CommonSshClient extends AbstractSshClient {

    private static final Logger logger = LoggerFactory.getLogger(CommonSshClient.class);

    public void login(String username, String password) throws Exception {
        this.getClient().authPassword(username, password);
        Session session = getClient().startSession();
        session.allocateDefaultPTY();
        this.setSession(session);
        Session.Shell shell = session.startShell();
        this.setShell(shell);
        this.processConnectedResult(this.getReader().readUntil("#"), writer);
    }

    public void send(String command) throws ServiceException {
        this.getWriter().write(command.trim());
        this.processResult(this.getReader().readUntil("#"), writer);
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

    /**
     * 密码输入后处理返回值 每个设备不同可重写
     *
     * @param result
     * @param writer
     * @throws ServiceException
     */
    public void processConnectedResult(String result, Writer writer) throws ServiceException {
        //SSH AuthUser 未抛出异常视为登录连接成功  无需处理welcome信息
    }

    @Override
    public String getReaderContent() {
        return super.getReaderContent().replace("---- More ----", "");
    }

    public static void main(String[] args) throws Exception {
        SshMessage message1 = new SshMessage();
        message1.setHostname("202.96.196.106");
        message1.setPort(22);
        message1.setUsername("zhongying");
        message1.setPassword("Yingzhong");
        message1.setRouteKey("test");
        LinkedList<String> list = new LinkedList<>();
        list.add("edit");
        list.add("services sp mitigation tms");
        list.add("show raw");
        message1.setCommands(list);
        SshQueue.getInstance().add("202.96.196.106", message1);
    }

}
