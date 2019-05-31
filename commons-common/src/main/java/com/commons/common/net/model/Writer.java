package com.commons.common.net.model;

import com.commons.common.net.AbstractNetClient;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;

import java.io.IOException;

/**
 * Copyright (C)
 * Writer
 * Author: jameslinlu
 */
public class Writer {

    private AbstractNetClient net = null;

    public Writer(AbstractNetClient net) {
        this.net = net;
    }

    public void write(String cmd) throws ServiceException {
        try {
            cmd += "\n";
            net.getOutput().write(cmd.getBytes());
            net.getOutput().flush();
        } catch (IOException e) {
            throw new ServiceException(ResultCode.ERROR_TELNET_WRITER, e);
        }
    }

}
