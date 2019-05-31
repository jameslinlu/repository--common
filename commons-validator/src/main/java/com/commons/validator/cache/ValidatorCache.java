package com.commons.validator.cache;

import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.validator.IValidator;
import com.commons.metadata.model.validator.model.ValidatorScriptItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright (C)
 * ValidatorCache
 * Author: jameslinlu
 */
public final class ValidatorCache {

    private static final Logger logger = LoggerFactory.getLogger(ValidatorCache.class);

    /**
     * method Sign : [ ..validatorImpl.class .. ]
     */
    static ConcurrentMap<String, List<IValidator>> cacheSignValidator = new ConcurrentHashMap<>();
    static ConcurrentMap<String, List<IValidator>> cacheUrlValidator = new ConcurrentHashMap<>();
    static ConcurrentMap<String, String> cacheUrlMethodSign = new ConcurrentHashMap<>();
    static ConcurrentMap<String, Map<String, List<ValidatorScriptItem>>> cacheScript = new ConcurrentHashMap<>();

    /**
     * 根据方法签名 获取校验集合
     *
     * @param methodSign
     * @return
     */
    public static List<IValidator> getValidatorBySign(String methodSign) {
        if (methodSign == null) {
            return new LinkedList<>();
        }
        return cacheSignValidator.get(methodSign);
    }

    /**
     * 根据方法签名获取 校验脚本
     *
     * @param methodSign
     * @return
     * @throws ServiceException
     */
    public static Map<String, List<ValidatorScriptItem>> getValidatorScriptBySign(String methodSign) throws ServiceException {
        if (methodSign == null) {
            return new HashMap<>();
        }
        return cacheScript.get(methodSign);
    }


    /**
     * 增加对url匹配后的补充验证
     * 例如 Controller已经注解Valid 并且 方法已经注解Valid 有正常的执行流程
     * 但是对 Op的Controller需要进行补充验证 (op中方法内的注解无法满足验证)
     * 此方法应当使用在无法操作注解下
     *
     * @param urlRegex   /api/user/save
     * @param validators
     */
    public static void setValidatorUrl(String urlRegex, List<IValidator> validators) {
        cacheUrlValidator.put(urlRegex, validators);
        cacheValidatorScript(urlRegex, validators);
    }

    public static List<IValidator> getValidatorByUrl(String url) {
        //合并满足url规则的校验集合
        List<IValidator> validators = new LinkedList<>();
        for (Map.Entry<String, List<IValidator>> valid : cacheUrlValidator.entrySet()) {
            Pattern pattern = Pattern.compile(valid.getKey());
            Matcher matcher = pattern.matcher(url);
            if (matcher.matches()) {
                validators.addAll(valid.getValue());
            }
        }
        return validators;
    }

    public static Map<String, List<ValidatorScriptItem>> getValidatorScriptByUrl(String url) {
        //合并满足url规则的校验脚本集合
        Map<String, List<ValidatorScriptItem>> scripts = new HashMap<>();
        for (Map.Entry<String, Map<String, List<ValidatorScriptItem>>> scriptEntry : cacheScript.entrySet()) {
            Pattern pattern = Pattern.compile(scriptEntry.getKey());
            Matcher matcher = pattern.matcher(url);
            if (matcher.matches()) {
                for (Map.Entry<String, List<ValidatorScriptItem>> fieldScript : scriptEntry.getValue().entrySet()) {
                    if (scripts.get(fieldScript.getKey()) == null) {
                        scripts.put(fieldScript.getKey(), new LinkedList<ValidatorScriptItem>());
                    }
                    scripts.get(fieldScript.getKey()).addAll(fieldScript.getValue());
                }
            }
        }
        return scripts;
    }


    /**
     * 设置 方法签名 和 校验集合
     *
     * @param methodSign
     * @param validators
     */
    public static void setValidatorSign(String methodSign, List<IValidator> validators) {
        cacheSignValidator.put(methodSign, validators);
        cacheValidatorScript(methodSign, validators);
    }


    /**
     * 根据 请求url 获取方法签名  以便调用 getValidatorBySign(String methodSign) 获取验证类
     *
     * @param url
     * @return
     */
    public static String getSign(String url) {
        String sign = null;
        for (Map.Entry<String, String> validScript : cacheUrlMethodSign.entrySet()) {
            Pattern pattern = Pattern.compile(validScript.getKey());
            Matcher matcher = pattern.matcher(url);
            if (matcher.matches()) {
                sign = validScript.getValue();
                break;
            }
        }
        return sign;
    }

    public static void setSign(String urlRegex, String methodSign) {
        cacheUrlMethodSign.put(urlRegex, methodSign);
    }


    private static void cacheValidatorScript(String methodSign, List<IValidator> validators) {
        Map<String, List<ValidatorScriptItem>> methodValidator = new HashMap<>();
        if (cacheScript.get(methodSign) == null) {
            for (IValidator validator : validators) {
                try {
                    //合并同字段校验规则
                    for (Map.Entry<String, List<ValidatorScriptItem>> validScript : validator.getValidatorScript().entrySet()) {
                        if (methodValidator.get(validScript.getKey()) == null) {
                            methodValidator.put(validScript.getKey(), new LinkedList<ValidatorScriptItem>());
                        }
                        if (validScript.getValue() != null) {
                            methodValidator.get(validScript.getKey()).addAll(new LinkedList<>(validScript.getValue()));
                        }
                    }
                } catch (ServiceException e) {
                    //continue
                    logger.error("Fail to Initialize IValidator Class {}", validator, e);
                    continue;
                }
            }
            cacheScript.put(methodSign, methodValidator);
            return;
        }
        if (methodValidator.isEmpty()) {
            return;
        }
        Map<String, List<ValidatorScriptItem>> alreadyMethodValidator = cacheScript.get(methodSign);
        for (Map.Entry<String, List<ValidatorScriptItem>> mv : methodValidator.entrySet()) {
            if (alreadyMethodValidator.get(mv.getKey()) != null) {
                alreadyMethodValidator.get(mv.getKey()).addAll(mv.getValue());
                continue;
            }
            alreadyMethodValidator.put(mv.getKey(), mv.getValue());
        }
        cacheScript.put(methodSign, alreadyMethodValidator);
    }


}
