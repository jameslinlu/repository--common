package com.commons.common.net.model;

import com.commons.common.net.AbstractNetClient;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Copyright (C)
 * Reader
 * Author: jameslinlu
 */
public class Reader {

    private static final Logger logger = LoggerFactory.getLogger(Reader.class);

    private AbstractNetClient net = null;

    private StringBuffer globalContent = new StringBuffer();

    public Reader(AbstractNetClient net) {
        this.net = net;
    }

    public String getContent() {
        return globalContent.toString();
    }

    private void covertAscii(StringBuffer content, int len) {
        switch (len) {
            case 27:
                content.append("ESC");
                this.globalContent.append("ESC");
                break;
            default:
                content.append((char) len);
                this.globalContent.append((char) len);
//                    System.out.print(String.valueOf((char) len));
                break;
        }
    }

    /**
     * 读取直到pattern匹配
     *
     * @param patterns
     * @return
     * @throws ServiceException
     */
    public String readUntil(String... patterns) throws ServiceException {
        StringBuffer content = new StringBuffer();
        try {
            int len = 0;
            do {
                len = net.getInput().read();
                if (len > 0) {
                    covertAscii(content, len);
                }
                if (patterns != null) {
                    boolean endBreak = false;
                    for (String pattern : patterns) {
                        if (pattern != null && content.toString().toLowerCase().endsWith(pattern.toLowerCase())) {
                            endBreak = true;
                            break;
                        }
                    }
                    if (endBreak) {
                        break;
                    }
                }
            }
            while (len >= 0);
            return content.toString();
        } catch (IOException e) {
            throw new ServiceException(ResultCode.ERROR_TELNET_READER, e);
        }
    }


}
