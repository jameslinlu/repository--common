package com.commons.metadata.model.validator.annotation;

import com.commons.metadata.model.validator.IValidator;

import java.lang.annotation.*;

/**
 * 校验注解 按class指定校验类
 * 需要校验的方法上增加的注解
 * Copyright (C)
 * Valid
 * Author: jameslinlu
 */
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Valid {
    Class<? extends IValidator>[] value() default {};
}
