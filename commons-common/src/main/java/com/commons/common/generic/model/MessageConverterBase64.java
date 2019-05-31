package com.commons.common.generic.model;

/**
 * CommonJsonHttpMessageConverter 鉴别返回对象
 */
public class MessageConverterBase64 {

    private Object body;

    public MessageConverterBase64(Object body) {
        this.body = body;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
