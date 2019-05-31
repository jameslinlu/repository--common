package com.commons.common.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.util.Locale;

/**
 * 配置文件初始化存取bean
 */
public class ContextUtil /*extends ApplicationObjectSupport*/ {


//    private static ApplicationContext applicationContext;
//
//    @PostConstruct
//    private void initialize() {
//        applicationContext = getApplicationContext();
//    }

    /**
     * 获取对象
     */
    public static <T> T getBean(Class<T> clazz) {
        return getWebAppContext().getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return getWebAppContext().getBean(name, clazz);
    }

    public static Object getBean(String name) {
        return getWebAppContext().getBean(name);
    }

    public static WebApplicationContext getWebAppContext() {
        return ContextLoader.getCurrentWebApplicationContext();
    }

    public static MessageSource getMessageSource() {
        return ContextUtil.getBean(MessageSource.class);
    }

    public static String getMessage(String code) {
        return getMessageSource().getMessage(code, (Object[]) null, code + " is undefined|999", LocaleContextHolder.getLocale());
    }

    public static String getMessage(String code, Object[] params,Locale locale) {
        return getMessageSource().getMessage(code, params, code + " is undefined|999", locale);
    }

}
