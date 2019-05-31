package com.commons.common.generic.annotation;

import com.commons.common.generic.enums.ResponseEncode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseBodyAttrs {

    /**
     * 响应转换编码
     *
     * @return
     */
    ResponseEncode encode() default ResponseEncode.NONE;

}
