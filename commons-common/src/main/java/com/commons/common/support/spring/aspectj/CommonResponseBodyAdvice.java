package com.commons.common.support.spring.aspectj;

import com.commons.common.generic.annotation.ResponseBodyAttrs;
import com.commons.common.generic.enums.ResponseEncode;
import com.commons.common.generic.model.MessageConverterBase64;
import com.commons.common.support.spring.converter.CommonJsonHttpMessageConverter;
import com.commons.common.utils.JSONUtils;
import com.commons.common.utils.PropUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


/**
 * Copyright (C)
 * Base64ResponseBodyAdviceController
 * Author: jameslinlu
 */
//@ControllerAdvice(basePackages = "com.zyuc.sh.telecom.ddos.web.open.controller")
public abstract class CommonResponseBodyAdvice implements ResponseBodyAdvice {

    /**
     * 具体项目中根据需要 设置默认的ResponseBodyAttr中的 encode等参数
     *
     * @return
     */
    public abstract ResponseEncode getDefaultEncode();
    public abstract boolean enableEncode();

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return CommonJsonHttpMessageConverter.class.isAssignableFrom(converterType)
                && returnType.getMethodAnnotation(ResponseBodyAttrs.class) != null;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null) {
            return body;
        }
        //启用@ResponseBody后有效
        ResponseBodyAttrs annotation = returnType.getMethodAnnotation(ResponseBodyAttrs.class);
        if (this.enableEncode()) {
            ResponseEncode encode = annotation.encode();
            if (this.getDefaultEncode() != null) {
                encode = this.getDefaultEncode();
            }
            switch (encode) {
                case BASE64://转换base64编码
                    return new MessageConverterBase64(body);
            }
        }
        return body;
    }
}
