package com.commons.dfs.client.impl;

import com.commons.dfs.config.CommonDfsConfig;
import com.commons.dfs.config.FastDfsConfig;
import com.commons.dfs.fastdfs.fish.*;
import com.commons.dfs.operations.IDfsOperation;
import com.commons.dfs.operations.impl.FastDfsOperation;
import com.commons.metadata.exception.ServiceException;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Copyright (C)
 * FastDfsClient
 * Author: jameslinlu
 */
public class FastDfsClient extends CommonDfsClient {

    private TrackerGroup trackerGroup;
    private TrackerClient trackerClient;

    public FastDfsClient() {
    }

    public FastDfsClient(CommonDfsConfig config) throws ServiceException {
        this.initialize(config);
    }

    public void initialize(CommonDfsConfig config) throws ServiceException {
        super.initialize(config);
        Object connectTimeSecond = config.getProperties().get(FastDfsConfig.CONNECT_TIMEOUT_KEY);
        if (connectTimeSecond == null) {
            connectTimeSecond = ClientGlobal.DEFAULT_CONNECT_TIMEOUT;
        }
        ClientGlobal.setG_connect_timeout(Integer.parseInt(connectTimeSecond.toString()) * 1000);

        Object networkTimeSecond = config.getProperties().get(FastDfsConfig.NETWORK_TIMEOUT_KEY);
        if (networkTimeSecond == null) {
            networkTimeSecond = ClientGlobal.DEFAULT_NETWORK_TIMEOUT;
        }
        ClientGlobal.setG_network_timeout(Integer.parseInt(networkTimeSecond.toString()) * 1000);
        ClientGlobal.setG_charset("UTF-8");

        Object trackerServerConfig = config.getProperties().get(FastDfsConfig.TRACKER_SERVER_KEY);
        if (trackerServerConfig == null) {
            throw new ServiceException("fastdfs tracker_server not found");
        }
        String[] trackerServerSeg = trackerServerConfig.toString().split(",");
        InetSocketAddress[] trackerServers = new InetSocketAddress[trackerServerSeg.length];
        String[] trackerServerPart = null;
        for (int i = 0; i < trackerServerSeg.length; i++) {
            trackerServerPart = trackerServerSeg[i].split("\\:", 2);
            if (trackerServerPart.length != 2) {
                throw new ServiceException("the value of item \"tracker_server\" is invalid, the correct format is host:port");
            }
            trackerServers[i] = new InetSocketAddress(trackerServerPart[0].trim(), Integer.parseInt(trackerServerPart[1].trim()));
        }

        ClientGlobal.setG_tracker_http_port(80);
        ClientGlobal.setG_anti_steal_token(false);

        trackerGroup = new TrackerGroup(trackerServers);
        trackerClient = new TrackerClient(trackerGroup);

    }

    /**
     * 通过此方法获取操作接口
     *
     * @return 操作接口实例
     * @throws ServiceException
     */
    public IDfsOperation getOpts() throws ServiceException {
        TrackerServer trackerServer = null;
        try {
            trackerServer = trackerClient.getConnection();
            if (trackerServer == null) {
                throw new ServiceException("FastDfs Client connection return Null");
            }
        } catch (IOException e) {
            throw new ServiceException(e);
        }
        StorageServer storageServer = null;
        try {
            storageServer = trackerClient.getStoreStorage(trackerServer);
            if (storageServer == null) {
                throw new ServiceException("FastDfs get Storage return Null");
            }
        } catch (IOException e) {
            throw new ServiceException(e);
        }
        return new FastDfsOperation(trackerServer, storageServer);
    }

    public void destroy() throws ServiceException {
        super.destroy();
        this.trackerClient = null;
        this.trackerGroup = null;
    }
}
