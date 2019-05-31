package com.commons.metadata.model.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Copyright (C)
 * HttpResponse
 * Author: jameslinlu
 */
public class HttpResponse implements Serializable {
    private static final String DEFAULT_CHARSET = "UTF-8";

    public HttpResponse() {
    }

    public HttpResponse(int code, byte[] bytes) {
        this.bytes = bytes;
        this.code = code;
    }

    private byte[] bytes;
    private int code;
    private String contentType;
    private Map<String,String> headers;

    public String getCharset() {
        if (contentType != null && contentType.lastIndexOf("=") != -1) {
            String charset = contentType.substring(contentType.lastIndexOf("=") + 1);
            try {
                Charset.forName(charset);
            } catch (Exception e) {
                return DEFAULT_CHARSET;
            }
        }
        return DEFAULT_CHARSET;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getText() {
        return this.getText(this.getCharset());
    }

    public String getText(String charset) {
        if (bytes == null) {
            return null;
        }
        return new String(bytes, Charset.forName(charset));
    }

    public InputStream getStream() {
        if (bytes == null) {
            return null;
        }
        return new ByteArrayInputStream(bytes);
    }

    public boolean isError() {
        return code >= 400;
    }
}
