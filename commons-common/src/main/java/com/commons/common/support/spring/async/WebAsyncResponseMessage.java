package com.commons.common.support.spring.async;

import com.commons.common.generic.model.ResponseErrorMessage;
import com.commons.common.generic.model.ResponseMessage;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import org.springframework.web.context.request.async.WebAsyncTask;

import java.util.concurrent.Callable;

/**
 * 异步响应消息 默认超时20秒
 * 返回对象ResponseMessage
 * Copyright (C)
 * ResponseMessageCallable
 * Author: jameslinlu
 */
public class WebAsyncResponseMessage extends WebAsyncTask<ResponseMessage> {

    public WebAsyncResponseMessage(String executorName, Callable<ResponseMessage> callable) {
        //timeout diff mvc:async-support,scope singleton
        //TODO
        super(3l * 60l * 1000l, executorName, callable);
        this.onTimeout(new Callable<ResponseMessage>() {
            @Override
            public ResponseMessage call() throws Exception {
                return new ResponseErrorMessage(new ServiceException(ResultCode.OPERATE_TIMEOUT));
            }
        });
    }
}
