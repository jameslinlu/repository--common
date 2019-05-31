package com.commons.proxy.center.transfer.model;

/**
 * Copyright (C)
 * TransferMessage
 * Author: jameslinlu
 */
public class TransferRequestMessage extends TransferMessage {
    public TransferRequestMessage() {
        super();
    }

    private String url;
    private String signature;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }


}
