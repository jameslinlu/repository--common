package com.commons.proxy.center.transfer.model;

/**
 * Copyright (C)
 * TransferMessage
 * Author: jameslinlu
 */
public class TransferResponseMessage extends TransferMessage {

    public TransferResponseMessage() {
        super();
    }

    private Object result;

    private Throwable exception;

    private Boolean publish;

    public Boolean getPublish() {
        return publish;
    }

    public void setPublish(Boolean publish) {
        this.publish = publish;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }
}
