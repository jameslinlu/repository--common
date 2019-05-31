package com.commons.dfs.operations;

import com.commons.metadata.exception.ServiceException;

/**
 * Copyright (C)
 * IDfsOperation
 * Author: jameslinlu
 */
public interface IDfsOperation {

    /**
     * 创建文件 输入文件字节 返回存储文件路径
     * 1.fastdfs = group+file_id
     *
     * @throws ServiceException
     */
    String createFile(byte[] fileBytes, String fileExtName) throws ServiceException;

    /**
     * 下载文件byte
     * 1.fastdfs path = group+file_id
     *
     * @return 文件字节
     * @throws ServiceException
     */
    byte[] readFile(String path) throws ServiceException;


    /**
     * 删除文件
     *
     * @param path 1.fastdfs path = group+file_id
     * @throws ServiceException
     */
    boolean deleteFile(String path) throws ServiceException;

}
