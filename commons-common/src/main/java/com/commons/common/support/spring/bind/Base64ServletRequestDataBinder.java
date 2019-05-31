package com.commons.common.support.spring.bind;

import com.commons.common.utils.Base64ParamUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;

import javax.servlet.ServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C)
 * Base64ServletRequestDataBinder
 * Author: jameslinlu
 */
public class Base64ServletRequestDataBinder extends ExtendedServletRequestDataBinder {

    private boolean allowPlain;//默认TRUE 允许接受非编码参数 若为false则只接受编码内的参数
    private String encodeParamName;


    public Base64ServletRequestDataBinder(Object target, String objectName, String encodeParamName, boolean allowPlain) {
        super(target, objectName);
        this.encodeParamName = encodeParamName;
        this.allowPlain = allowPlain;
    }

    @Override
    protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {
        super.addBindValues(mpvs, request);
        if (!allowPlain && mpvs.getPropertyValueList() != null) {
            List<PropertyValue> deleteValues = new ArrayList<>();
            for (PropertyValue propertyValue : mpvs.getPropertyValueList()) {
                //不接受非编码内参数时删除
                if (BeanUtils.isSimpleProperty(propertyValue.getValue().getClass())) {
                    deleteValues.add(propertyValue);
                }
            }
            for (PropertyValue deleteValue : deleteValues) {
                mpvs.removePropertyValue(deleteValue.getName());
            }
        }
        if (StringUtils.hasText(request.getParameter(encodeParamName))) {
            Map<String, Object> result = Base64ParamUtils.getMapValues(request.getParameter(encodeParamName));
            for (Map.Entry<String, Object> kv : result.entrySet()) {
                if (kv.getValue() != null && kv.getValue().getClass().isAssignableFrom(String[].class)) {
                    for (String value : (String[]) kv.getValue()) {
                        mpvs.addPropertyValue(kv.getKey(), value);
                    }
                }
                if (kv.getValue() != null && kv.getValue().getClass().isAssignableFrom(String.class)) {
                    mpvs.addPropertyValue(kv.getKey(), kv.getValue());
                }
            }
        }
    }
}
