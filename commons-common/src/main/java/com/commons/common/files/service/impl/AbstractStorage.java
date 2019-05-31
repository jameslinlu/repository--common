package com.commons.common.files.service.impl;

import com.commons.common.files.model.UploadFile;
import com.commons.common.files.model.UploadResult;
import com.commons.common.files.service.IUpload;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Copyright (C)
 * AbstractStorage
 * Author: jameslinlu
 */
public abstract class AbstractStorage implements IUpload {

    private static Logger logger = LoggerFactory.getLogger(AbstractStorage.class);


    private List<String> permitSuffies;//允许上传文件后缀
    private Integer permitSize;//允许上传文件大小
    private List<UploadFile> denySizeFile = new ArrayList<UploadFile>();//拒绝 大小超限文件
    private List<UploadFile> denySuffixFile = new ArrayList<UploadFile>();//拒绝 后缀不对文件


    @Override
    public void setPermitSuffix(String[] suffies) {
        permitSuffies = Arrays.asList(suffies);
    }

    @Override
    public void setPermitSize(Integer size) {
        permitSize = size;
    }

    public List<String> getPermitSuffies() {
        return permitSuffies;
    }

    public void setPermitSuffies(List<String> permitSuffies) {
        this.permitSuffies = permitSuffies;
    }

    public Integer getPermitSize() {
        return permitSize;
    }

    public List<UploadFile> getDenySuffixFile() {
        return denySuffixFile;
    }

    public void setDenySuffixFile(List<UploadFile> denySuffixFile) {
        this.denySuffixFile = denySuffixFile;
    }

    public List<UploadFile> getDenySizeFile() {
        return denySizeFile;
    }

    public void setDenySizeFile(List<UploadFile> denySizeFile) {
        this.denySizeFile = denySizeFile;
    }

    /**
     * 返回所有禁止上传文件列表
     *
     * @return
     */
    public List<UploadFile> getDenyFile() {
        denySizeFile.addAll(denySuffixFile);
        return denySizeFile;
    }

    public void throwMessage() throws ServiceException {
        StringBuffer msg = new StringBuffer();
        if (!denySizeFile.isEmpty()) {
            throw new ServiceException(ResultCode.ERROR_FILE_SIZE);
        }
        if (!denySuffixFile.isEmpty()) {
            throw new ServiceException(ResultCode.ERROR_FILE_SUFFIX);
        }
    }

    public UploadResult uploads(HttpServletRequest request) throws ServiceException {
        return this.uploads(request, false);
    }

    public UploadResult uploads(HttpServletRequest request, boolean throwError) throws ServiceException {
        Map<String, List<UploadFile>> resultmap = this.uploadFile(request);
        if (throwError) {
            this.throwMessage();
        }
        UploadResult result = new UploadResult();
        result.setFiles(resultmap);
        return result;
    }

    /**
     * 获取待处理文件组
     *
     * @return 处理文件组
     */
    public List<MultipartFile> todoFiles(HttpServletRequest request) {
        //文件待处理list
        List<MultipartFile> fileList = new LinkedList<MultipartFile>();
        //转换上传Request
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        //循环上传文件名称
        for (Iterator it = multipartRequest.getFileNames(); it.hasNext(); ) {
            //上传表单name
            String key = (String) it.next();
            //根据表单name获取文件组
            List<MultipartFile> files = multipartRequest.getFiles(key);
            //循环文件组
            for (MultipartFile file : files) {
                if (file.getOriginalFilename().length() > 0 && file.getOriginalFilename().lastIndexOf(".") != -1) {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }

}
