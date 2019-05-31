package com.commons.common.support.spring.async;

import com.commons.common.generic.model.ResponseMessage;
import com.commons.common.utils.ContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Controller 返回值拦截 ResponseMessage均为non-block
 * Copyright (C)
 * CommonListenableFutureReturnValueHandler
 * Author: jameslinlu
 */
public class ResponseMessageFutureReturnValueHandler implements HandlerMethodReturnValueHandler {

    private static final Logger logger = LoggerFactory.getLogger(ResponseMessageFutureReturnValueHandler.class);

    private String executorName;

    public void setExecutorName(String executorName) {
        this.executorName = executorName;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return ResponseMessage.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public void handleReturnValue(final Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if (returnValue == null) {
            mavContainer.setRequestHandled(true);
            return;
        }

        WebAsyncTask<?> webAsyncTask = new WebAsyncResponseMessage(executorName, new ResponseMessageCallable() {
            @Override
            public ResponseMessage execute() {
                return (ResponseMessage) returnValue;
            }
        });

        webAsyncTask.setBeanFactory(ContextUtil.getWebAppContext());
        WebAsyncUtils.getAsyncManager(webRequest).startCallableProcessing(webAsyncTask, mavContainer);

    }
}
