package com.commons.dfs.client.impl;

import com.commons.dfs.client.IDfsClient;
import com.commons.dfs.config.CommonDfsConfig;
import com.commons.metadata.exception.ServiceException;

/**
 * Copyright (C)
 * CommonDfsClient
 * Author: jameslinlu
 */
public abstract class CommonDfsClient implements IDfsClient {

    protected CommonDfsConfig config = null;

    public void initialize(CommonDfsConfig config) throws ServiceException {
        this.config = config;
    }

    public void destroy() throws ServiceException {
        this.config = null;
    }
}
