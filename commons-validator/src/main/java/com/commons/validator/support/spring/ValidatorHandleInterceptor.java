package com.commons.validator.support.spring;

import com.commons.common.generic.model.ResponseErrorMessage;
import com.commons.common.utils.Base64ParamUtils;
import com.commons.common.utils.WebUtil;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.validator.IValidator;
import com.commons.validator.cache.ValidatorCache;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 校验访问请求
 * Copyright (C)
 * ValidatorHandleInterceptor
 * Author: jameslinlu
 */
public class ValidatorHandleInterceptor extends HandlerInterceptorAdapter {


    private String encodeParamName = "request";

    public void setEncodeParamName(String encodeParamName) {
        this.encodeParamName = encodeParamName;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //对Web请求 拦截获取MethodSign
        //1.  mapping中匹配
        //2.  获取指定方法 sign
        //3.  从cache中取校验内容
        String url = WebUtil.getRequestURL().substring(WebUtil.getRequestRoot().length() + WebUtil.getContextPath().length());
        String sign = ValidatorCache.getSign(url);

        List<IValidator> validatorsSign = ValidatorCache.getValidatorBySign(sign);
        List<IValidator> validatorsUrl = ValidatorCache.getValidatorByUrl(url);
        if (validatorsSign.isEmpty() && validatorsUrl.isEmpty()) {
            return true;
        }
        List<IValidator> validators = new LinkedList<>();
        validators.addAll(validatorsSign);
        validators.addAll(validatorsUrl);
        for (IValidator validator : validators) {
            try {
                this.process(request, validator);
            } catch (ServiceException e) {
                WebUtil.responseJSON(new ResponseErrorMessage(ResultCode.ERROR_PARAMETER, validator.getValidatorMessage()));
                return false;
            }
        }
        return true;
    }

    public void process(HttpServletRequest request, IValidator validator) throws ServiceException {
        //整合加密参数 和 非加密参数,非加密参数无法覆盖加密参数
        String encodeParameter = request.getParameter(encodeParamName);
        Map<String, Object> params = new HashMap<>();
        if (encodeParameter != null) {
            params = Base64ParamUtils.getMapValues(request.getParameter(encodeParamName));
            for (String key : params.keySet()) {
                String[] arrays = null;
                if (!params.get(key).getClass().isAssignableFrom(String[].class)) {
                    arrays = new String[]{params.get(key).toString()};
                }
                params.put(key, arrays);
            }
        }
        Map<String, String[]> plainParams = request.getParameterMap();
        for (String plainKey : plainParams.keySet()) {
            if (params.get(plainKey) == null) {
                params.put(plainKey, plainParams.get(plainKey));
            }
        }
        validator.process(params);
    }


}
