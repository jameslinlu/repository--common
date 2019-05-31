package com.commons.common.support.spring.bind;

import com.commons.common.utils.Base64ParamUtils;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;

import javax.servlet.http.HttpServletRequest;

/**
 * Copyright (C)
 * SecureRequestParamMethodArgumentResolver
 * Author: jameslinlu
 */
public class SecureRequestParamMethodArgumentResolver extends RequestParamMethodArgumentResolver {

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

    public SecureRequestParamMethodArgumentResolver() {
        super(true);
    }

    public SecureRequestParamMethodArgumentResolver(String encodeParamName, boolean allowPlain) {
        super(true);
        this.encodeParamName = encodeParamName;
        this.allowPlain = allowPlain;
    }


    @Override
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest webRequest) throws Exception {
        Object object = super.resolveName(name, parameter, webRequest);
        if(!enable){
            return object;
        }
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (object == null || (object != null && object.getClass().isAssignableFrom(String.class) && !allowPlain)) {
            if (!StringUtils.hasText(request.getParameter(encodeParamName))) {
                return null;
            }
            Object value = Base64ParamUtils.getMapValues(request.getParameter(encodeParamName)).get(name);
            if (value != null && value.getClass().isAssignableFrom(String[].class)) {
                return ((String[]) value)[0];
            }
            if (value != null && value.getClass().isAssignableFrom(String.class)) {
                return value;
            }
            return null;
        }
        return object;
    }
}
