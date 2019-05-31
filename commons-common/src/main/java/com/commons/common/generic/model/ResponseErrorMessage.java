package com.commons.common.generic.model;

/**
 * handler error message
 */
public class ResponseErrorMessage extends ResponseMessage {
    public ResponseErrorMessage(Exception e) {
        super(e);
    }

    public ResponseErrorMessage(String resultCode, Object body) {
        super(resultCode, body);
    }
}
