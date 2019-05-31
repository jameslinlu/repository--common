package com.commons.common.net.telnet;

import com.commons.common.net.AbstractNetClient;
import com.commons.common.net.model.Reader;
import com.commons.common.net.model.Writer;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Copyright (C)
 * CommonTelnetClient
 * Author: jameslinlu
 */
public abstract class AbstractTelnetClient extends AbstractNetClient {

    private static final Logger logger = LoggerFactory.getLogger(AbstractTelnetClient.class);

    public AbstractTelnetClient() {

    }

    protected TelnetClient client = null;

    public OutputStream getOutput() {
        return client.getOutputStream();
    }

    public InputStream getInput() {
        return client.getInputStream();
    }

    public void initialize() throws ServiceException {
        client = new TelnetClient();
        client.setConnectTimeout(1115 * 1000);
        client.setDefaultTimeout(1115 * 1000);
        this.registerOptionHandler();
        this.registerSpyStream();
        this.reader = new Reader(this);
        this.writer = new Writer(this);
    }

    private void registerOptionHandler() {
        try {
            TerminalTypeOptionHandler ttopt = new TerminalTypeOptionHandler("VT100", false, false, true, false);
            EchoOptionHandler echoopt = new EchoOptionHandler(true, false, true, false);
            SuppressGAOptionHandler gaopt = new SuppressGAOptionHandler(true, true, true, true);
            client.addOptionHandler(ttopt);
            client.addOptionHandler(echoopt);
            client.addOptionHandler(gaopt);
        } catch (Exception e) {
            logger.error("CommonTelnetClient registerHandler Fail", e);
        }
    }

    private void registerSpyStream() {
        FileOutputStream out = null;
        try {
            File file = File.createTempFile("telnet", ".cmd");
            out = new FileOutputStream(file);
            logger.debug("CommonTelnetClient Create TempFile At {}", file.getAbsolutePath());
        } catch (IOException e) {
            logger.error("CommonTelnetClient Create TempFile Fail", e);
        }
        client.registerSpyStream(out);
    }


    public void connect(String hostname, Integer port) throws ServiceException {
        try {
            client.connect(hostname, port);
        } catch (IOException e) {
            logger.error("CommonTelnetClient Connect Fail", e);
            throw new ServiceException(ResultCode.ERROR_TELNET_CONNECT, e);
        }
        this.processResult(reader.readUntil(":"), writer);
    }


    public void disconnect() throws ServiceException {
        try {
            client.disconnect();
            client = null;
        } catch (IOException e) {
            logger.error("CommonTelnetClient DisConnect Fail", e);
            throw new ServiceException(ResultCode.ERROR_TELNET_DISCONNECT, e);
        }
    }

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
