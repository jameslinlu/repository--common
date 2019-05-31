package com.commons.common.support.spring.converter;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.commons.common.generic.model.MessageConverterBase64;
import com.commons.common.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Copyright (C)
 * CommonJsonHttpMessageConverter
 * Author: jameslinlu
 */
public class CommonJsonHttpMessageConverter extends FastJsonHttpMessageConverter {


    private static final Logger logger = LoggerFactory.getLogger(CommonJsonHttpMessageConverter.class);
    private String encodeParamName;

    public void setEncodeParamName(String encodeParamName) {
        this.encodeParamName = encodeParamName;
    }

    @Override
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        /**
         * MessageConverterBase64 将返回转换格式为 {response:'base64 Object String'}
         */
        if (obj != null && obj.getClass().isAssignableFrom(MessageConverterBase64.class)) {
            try {
                MessageConverterBase64 msg = (MessageConverterBase64) obj;
                OutputStream out = outputMessage.getBody();
                JSONUtils.writeBytesValueJSON(encodeParamName, msg.getBody(), out);
            } catch (Exception e) {
                logger.error("Base64 Json Write Fail", e);
            }
            return;
        }
        //扩展其他json编码转换格式
        super.writeInternal(obj, outputMessage);


    }

}
