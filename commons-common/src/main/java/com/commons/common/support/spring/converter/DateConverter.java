package com.commons.common.support.spring.converter;

import com.alibaba.fastjson.util.TypeUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

public class DateConverter implements Converter<String, Date> {
    @Override
    public Date convert(String source) {
        return TypeUtils.castToDate(source);
    }
}
