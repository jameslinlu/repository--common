package com.commons.common.support.spring.aspectj;

import com.commons.common.utils.Reflections;
import com.commons.metadata.model.Model;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * 业务默认参数AOP
 * Copyright (C)
 * Logger
 * Author: jameslinlu
 */
public abstract class DefaultArgumentsAspectj {


    private static final Logger logger = LoggerFactory.getLogger(DefaultArgumentsAspectj.class);

    /**
     * 處理model參數
     *
     * @param modelArg
     */
    public abstract void process(Object modelArg, ProceedingJoinPoint pjp);

    /**
     * 默认参数AOP处理
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        try {
            Object[] args = pjp.getArgs();
            if (args != null && args.length > 0) {
                for (Object arg : pjp.getArgs()) {
                    if (arg != null && Model.class.isAssignableFrom(arg.getClass())) {
                        this.process(arg, pjp);
                    }
                }
            }
            return pjp.proceed();
        } catch (Throwable ex) {
            throw ex;
        }
    }

    public void set(Object arg, String fieldName, Object value) {
        Field field = Reflections.getAccessibleField(arg, fieldName);
        if (field != null) {
            try {
                field.set(arg, value);
            } catch (IllegalAccessException e) {
                logger.error("Default Args set Fail:{}", e.getMessage());
            }
        }
    }

}
