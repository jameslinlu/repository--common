package com.commons.proxy.center.transfer.model;


import org.apache.http.entity.ContentType;

import java.io.Serializable;

/**
 * Copyright (C)
 * TransferContentType
 * Author: jameslinlu
 */
public class TransferContentType implements Serializable {
    public static final ContentType APPLICATION_COMMON;

    static {
        APPLICATION_COMMON = ContentType.create("x-application/common", "UTF-8");
    }
}
