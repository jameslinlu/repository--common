package com.commons.common.files.service.impl;

import com.commons.common.files.model.UploadFile;
import com.commons.common.utils.DateUtil;
import com.commons.common.utils.WebUtil;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UploadLocalStorage extends AbstractStorage {

    private static Logger logger = LoggerFactory.getLogger(UploadLocalStorage.class);

    //从properties获取配置的根路径  在部署和开发时  可以直接切换 为空时默认为项目根路径  linux配置为/
    private String root;
    //在root后自定义文件夹路径
    private String folder;
    //在文件夹路径后自定义是否开启 日期格式目录层级
    private boolean enableDateFolder;

    //启用 年月日 路径格式
    public void setEnableDateFolder(boolean enableDateFolder) {
        this.enableDateFolder = enableDateFolder;
    }

    //上传目录
    public void setRoot(String root) {
        this.root = root;
    }

    //上传文件夹
    public void setFolder(String folder) {
        this.folder = folder;
    }

    /**
     * 获取上传根路径
     * 默认项目下
     */
    public String getUploadRootPath() {
        if (root == null) {
            root = "";
        }
        //为空 或者 有相对路径
        if (root.trim().equals("") || root.indexOf("..") != -1) {
            return WebUtil.getWebRoot(root) + File.separator;
        }
        return root;
    }

    /**
     * 获取上传文件夹路径
     */
    private String getUploadFolderPath() {
        if (folder == null) {
            return "";
        }
        return folder;
    }


    /**
     * 上传文件
     */
    public Map<String, List<UploadFile>> uploadFile(HttpServletRequest request) throws ServiceException {
        Map<String, List<UploadFile>> resultMap = null;

        try {
            //文件返回list
            resultMap = new HashMap<>();

            //获取待处理文件组
            List<MultipartFile> todoFileList = todoFiles(request);

            //获取上传根路径
            String rootPath = getUploadRootPath();

            //获取上传文件夹路径,由常量反射
            String folderPath = getUploadFolderPath();
            if (enableDateFolder) {
                folderPath += DateUtil.getNowTime("/yyyy/MM/dd");
            }
            //判断并创建文件夹
            String root_folder_path = rootPath + folderPath + File.separator;
            File root_folder_exist = new File(root_folder_path);
            if (!root_folder_exist.exists()) {
                root_folder_exist.mkdirs();
            }

            //保存文件
            for (MultipartFile todoFile : todoFileList) {
                String inputName = ((CommonsMultipartFile) todoFile).getFileItem().getFieldName();
                String rawname = todoFile.getOriginalFilename();// 文件原名称 含后缀
                String suffix = rawname.substring(rawname.lastIndexOf(".") + 1);// 文件后缀
                String size = new BigDecimal(todoFile.getSize()).divide(new BigDecimal(1024), 2, RoundingMode.HALF_DOWN).toString();// 文件大小 /1024 = kb  /1024/1024=mb
                String name = DateUtil.getNowTime("yyyyMMddHHmmss") + RandomStringUtils.randomNumeric(6);// 文件转码后名称 含后缀
                String filepath = folderPath + File.separator + name + "." + suffix;// 文件路径

                UploadFile file = new UploadFile();
                file.setFilepath(filepath.replaceAll("\\\\", "/"));
                file.setName(name);
                file.setRawname(rawname);
                file.setSize(size);
                file.setSuffix(suffix);
                file.setFullPath(rootPath);


                if (getPermitSize() != null && todoFile.getSize() > getPermitSize()) {
                    getDenySizeFile().add(file);
                    continue;
                }
                if (getPermitSuffies() != null && !getPermitSuffies().contains(suffix)) {
                    getDenySuffixFile().add(file);
                    continue;
                }

                //保存文件
                try {
                    File outFile = new File(rootPath + filepath);
                    todoFile.transferTo(outFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                logger.info(file.toString());
                if (resultMap.get(inputName) == null) {
                    resultMap.put(inputName, new LinkedList<UploadFile>());
                }
                resultMap.get(inputName).add(file);
            }
        } catch (IllegalStateException e) {
            throw new ServiceException(ResultCode.ERROR_FILE);
        }

        return resultMap;
    }


}
