package com.commons.common.support.spring.async;

import com.commons.common.generic.model.ResponseMessage;

import java.util.concurrent.Callable;

/**
 * 指定callback类型 屏蔽异常
 * Copyright (C)
 * ResponseMessageCallable
 * Author: jameslinlu
 */
public abstract class ResponseMessageCallable implements Callable<ResponseMessage> {
    @Override
    public ResponseMessage call() throws Exception {
        return execute();
    }

    public abstract ResponseMessage execute();
}
