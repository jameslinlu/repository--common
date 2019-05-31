package com.commons.common.files.service;

import com.commons.common.files.model.UploadFile;
import com.commons.common.files.model.UploadResult;
import com.commons.metadata.exception.ServiceException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


/**
 * 上传接口
 */
public interface IUpload {


    /**
     * 获取待上传文件list
     */
    public List<MultipartFile> todoFiles(HttpServletRequest request);

    /**
     * 上传文件  请求包含一个或多个文件
     *
     * @return 多个文件对象
     */
    Map<String, List<UploadFile>> uploadFile(HttpServletRequest request) throws ServiceException;

    /**
     * 上传文件并获取异常信息
     *
     * @return map: {data:[{},{},{}],msg:'xxx'}
     */
    UploadResult uploads(HttpServletRequest request) throws ServiceException;

    UploadResult uploads(HttpServletRequest request, boolean throwError) throws ServiceException;

    /**
     * 获取上传根路径
     *
     * @return 根路径
     */
//    public String getUploadRootPath();

    /**
     * 硬编码文件夹路径
     */
//    public void setFolder(String folder);

//    public void setRoot(String root);

    /**
     * 设置允许上传后缀
     */
    public void setPermitSuffix(String[] suffies);

    /**
     * 设置允许上传大小（MB）
     */
    public void setPermitSize(Integer size);

    /**
     * 所有限制文件list
     */
    public List<UploadFile> getDenyFile();

    /**
     * 错误文件提示
     */
    void throwMessage() throws ServiceException;

}
