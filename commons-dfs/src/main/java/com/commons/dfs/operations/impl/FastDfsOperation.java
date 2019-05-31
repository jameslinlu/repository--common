package com.commons.dfs.operations.impl;

import com.commons.dfs.fastdfs.fish.StorageClient1;
import com.commons.dfs.fastdfs.fish.StorageServer;
import com.commons.dfs.fastdfs.fish.TrackerServer;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;

/**
 * Copyright (C)
 * FastDfsOperation
 * Author: jameslinlu
 */
public class FastDfsOperation extends CommonDfsOperation {
    private TrackerServer trackerServer;
    private StorageServer storageServer;

    public FastDfsOperation() {
        /**
         *  扩展描述
         *  Operation接口 抽象扩展
         *
         *  operator.upload_file()=createFile 创建文件
         *  operator.download_file()=getFile  获取文件
         *  operator.get_file_info1()+operator.get_metadata1()=getFileInfo 获取文件mate信息
         *  operator.upload_appender_file()=createAppendFile 创建追加型文件
         *  operator.append_file1()=appendFile 对文件追加
         *  operator.delete_file1()=deleteFile 删除文件
         *  operator.truncate_file()=truncateFile 清空文件
         *
         */
    }

    public FastDfsOperation(TrackerServer trackerServer, StorageServer storageServer) {
        this.trackerServer = trackerServer;
        this.storageServer = storageServer;
    }


    @Override
    public boolean deleteFile(String path) throws ServiceException {
        StorageClient1 operator = null;
        try {
            operator = new StorageClient1(trackerServer, storageServer);
            int res = operator.delete_file1(path);
            if (res != 0) {
                throw new ServiceException(ResultCode.ERROR_DFS_DELETE);
            }
            return res == 0;
        } catch (Exception e) {
            throw new ServiceException(e);
        } finally {
            if (operator != null) {
                operator.close();
            }
        }
    }

    @Override
    public byte[] readFile(String path) throws ServiceException {
        StorageClient1 operator = null;
        try {
            operator = new StorageClient1(trackerServer, storageServer);
            byte[] bytes = null;
            bytes = operator.download_file1(path);
            if (bytes == null) {
                throw new ServiceException(ResultCode.ERROR_DFS_READ);
            }
            return bytes;
        } catch (Exception e) {
            throw new ServiceException(e);
        } finally {
            if (operator != null) {
                operator.close();
            }
        }
    }

    @Override
    public String createFile(byte[] fileBytes, String fileExtName) throws ServiceException {
        StorageClient1 operator = null;
        try {
            operator = new StorageClient1(trackerServer, storageServer);
            String path = operator.upload_file1(fileBytes, fileExtName, null);
            if (path == null) {
                throw new ServiceException(ResultCode.ERROR_DFS_CREATE);
            }
            return path;
        } catch (Exception e) {
            throw new ServiceException(e);
        } finally {
            if (operator != null) {
                operator.close();
            }
        }
    }


}
