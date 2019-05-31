package com.commons.proxy.center.model;

/**
 * Copyright (C)
 * RequestAuthorizeUtil
 * Author: jameslinlu
 */
public class RequestAuthorize {

    public static final String PROXY_ID = "proxy-requestId";
    public static final String PROXY_SIGN = "proxy-signature";
    public static final String PROXY_TIME = "proxy-timestamp";


    private String id;
    private String signature;
    private String timestamp;

    public RequestAuthorize() {
    }

    public RequestAuthorize(String id, String signature, String timestamp) {
        this.id = id;
        this.signature = signature;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


}
