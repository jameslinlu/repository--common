package com.commons.common.utils;

import com.commons.common.files.model.UploadResult;
import com.commons.common.files.service.IUpload;
import com.commons.metadata.exception.ServiceException;

import javax.servlet.http.HttpServletRequest;

/**
 * Copyright (C)
 * UploadUtils
 * Author: jameslinlu
 */
public class UploadUtils {

    private static IUpload upload;

    public static UploadResult process(HttpServletRequest request) throws ServiceException {
        upload = ContextUtil.getBean(IUpload.class);
        return upload.uploads(request);
    }

    public static UploadResult process(String name, HttpServletRequest request) throws ServiceException {
        upload = ContextUtil.getBean(name, IUpload.class);
        return upload.uploads(request);
    }

    public static UploadResult process(String name, HttpServletRequest request, boolean throwError) throws ServiceException {
        upload = ContextUtil.getBean(name, IUpload.class);
        return upload.uploads(request, throwError);
    }
}
