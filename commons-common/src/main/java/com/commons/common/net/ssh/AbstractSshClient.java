package com.commons.common.net.ssh;

import com.commons.common.net.AbstractNetClient;
import com.commons.common.net.model.Reader;
import com.commons.common.net.model.Writer;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PublicKey;

/**
 * Copyright (C)
 * CommonTelnetClient
 * Author: jameslinlu
 */
public abstract class AbstractSshClient extends AbstractNetClient {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSshClient.class);
    protected SSHClient client;
    protected Session session;
    protected Session.Shell shell;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Session.Shell getShell() {
        return shell;
    }

    public void setShell(Session.Shell shell) {
        this.shell = shell;
    }

    public OutputStream getOutput() {
        return shell.getOutputStream();
    }

    public InputStream getInput() {
        return shell.getInputStream();
    }

    public AbstractSshClient() {
    }

    public SSHClient getClient() {
        return client;
    }

    public void initialize() throws ServiceException {
        client = new SSHClient();
        client.addHostKeyVerifier(
                new HostKeyVerifier() {
                    @Override
                    public boolean verify(String s, int i, PublicKey publicKey) {
                        return true;
                    }
                });

        this.reader = new Reader(this);
        this.writer = new Writer(this);
    }


    public void connect(String hostname, Integer port) throws ServiceException {
        try {
            client.connect(hostname, port);
        } catch (IOException e) {
            logger.error("CommonSshClient Connect Fail", e);
            throw new ServiceException(ResultCode.ERROR_TELNET_CONNECT, e);
        }
    }


    public void disconnect() throws ServiceException {
        try {
            if (session != null) {
                session.close();
            }
            if (client != null) {
                client.disconnect();
            }
        } catch (IOException e) {
            logger.error("CommonSshClient DisConnect Fail", e);
            throw new ServiceException(ResultCode.ERROR_TELNET_DISCONNECT, e);
        }
    }

    /**
     * 发送命令
     *
     * @param command
     * @throws ServiceException
     */
    public abstract void send(String command) throws ServiceException;

    /**
     * 登陆
     *
     * @param username
     * @param password
     * @throws Exception
     */
    public abstract void login(String username, String password) throws Exception;

    /**
     * 密码输入后处理返回值 每个设备不同可重写
     *
     * @param result
     * @param writer
     * @throws ServiceException
     */
    public abstract void processConnectedResult(String result, Writer writer) throws ServiceException;


    /**
     * 处理普通命令结果返回 对应长命令可write输入more或其他读取更多 每个设备不同可重写
     *
     * @param result
     * @param writer
     */
    public abstract void processResult(String result, Writer writer) throws ServiceException;


}
