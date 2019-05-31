package com.commons.common.support.spring.bind;

import com.commons.common.utils.PropUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

/**
 * Copyright (C)
 * SecureServletModelAttributeMethodProcessor
 * Author: jameslinlu
 */
public class SecureServletModelAttributeMethodProcessor extends ServletModelAttributeMethodProcessor {

    @Autowired
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    private boolean allowPlain = true;//默认TRUE 允许接受非编码参数 若为false则只接受编码内的参数
    private String encodeParamName = "request";
    private boolean enable = true;//是否启用

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setAllowPlain(boolean allowPlain) {
        this.allowPlain = allowPlain;
    }

    public void setEncodeParamName(String encodeParamName) {
        this.encodeParamName = encodeParamName;
    }

    public SecureServletModelAttributeMethodProcessor() {
        super(true);
    }

    public SecureServletModelAttributeMethodProcessor(String encodeParamName, boolean allowPlain) {
        super(true);
        this.encodeParamName = encodeParamName;
        this.allowPlain = allowPlain;
    }


    @Override
    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
        if (this.enable) {
            Object target = binder.getTarget();
            Base64ServletRequestDataBinder dataBinder = new Base64ServletRequestDataBinder(target, binder.getObjectName(), this.encodeParamName, this.allowPlain);
            //for init bind
            requestMappingHandlerAdapter.getWebBindingInitializer().initBinder(dataBinder, request);
            super.bindRequestParameters(dataBinder, request);
            return;
        }
        super.bindRequestParameters(binder, request);
    }


}
