package com.commons.validator.support.spring;

import com.commons.common.generic.annotation.ResponseBodyAttrs;
import com.commons.common.generic.model.ResponseErrorMessage;
import com.commons.common.generic.model.ResponseMessage;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.log.annotation.Log;
import com.commons.metadata.model.validator.model.ValidatorScriptItem;
import com.commons.validator.cache.ValidatorCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 提供前端校验script脚本
 * @Controller 此处由集成后的实现类 自定义url
 * @RequestMapping("/api/validator")
 * Copyright (C)
 * ValidatorController
 * Author: jameslinlu
 */
public class ValidatorScriptController {

    private static final Logger logger = LoggerFactory.getLogger(ValidatorScriptController.class);

    @RequestMapping(value = "/script", method = RequestMethod.GET)
    @ResponseBody
    @ResponseBodyAttrs
    @Log(description = "获取校验脚本")
    public ResponseMessage script(String key) {
        try {
            String sign = ValidatorCache.getSign(key);
            Map<String, List<ValidatorScriptItem>> scriptSign = ValidatorCache.getValidatorScriptBySign(sign);
            Map<String, List<ValidatorScriptItem>> scriptUrl = ValidatorCache.getValidatorScriptByUrl(key);
            Map<String, List<ValidatorScriptItem>> script = this.combine(scriptSign, scriptUrl);
            return new ResponseMessage(script);
        } catch (ServiceException e) {
            return new ResponseErrorMessage(e);
        }
    }

    private Map<String, List<ValidatorScriptItem>> combine(Map<String, List<ValidatorScriptItem>>... fieldScripts) {
        Map<String, List<ValidatorScriptItem>> results = new HashMap<>();
        for (Map<String, List<ValidatorScriptItem>> scriptMap : fieldScripts) {
            for (Map.Entry<String, List<ValidatorScriptItem>> scriptEntry : scriptMap.entrySet()) {
                if (results.get(scriptEntry.getKey()) == null) {
                    results.put(scriptEntry.getKey(), new LinkedList<ValidatorScriptItem>());
                }
                results.get(scriptEntry.getKey()).addAll(scriptEntry.getValue());
            }
        }
        return results;
    }
}
