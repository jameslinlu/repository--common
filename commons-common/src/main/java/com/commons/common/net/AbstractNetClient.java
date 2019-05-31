package com.commons.common.net;

import com.commons.common.net.model.Reader;
import com.commons.common.net.model.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Copyright (C)
 * CommonTelnetClient
 * Author: jameslinlu
 */
public abstract class AbstractNetClient {

    private static final Logger logger = LoggerFactory.getLogger(AbstractNetClient.class);

    public AbstractNetClient() {

    }

    public String getReaderContent() {
        return reader.getContent();
    }

    protected Reader reader = null;
    protected Writer writer = null;

    public Reader getReader() {
        return reader;
    }

    public Writer getWriter() {
        return writer;
    }

    public abstract OutputStream getOutput();

    public abstract InputStream getInput();

}
