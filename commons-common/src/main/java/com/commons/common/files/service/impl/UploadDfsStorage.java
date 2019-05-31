package com.commons.common.files.service.impl;

import com.commons.common.files.model.UploadFile;
import com.commons.common.utils.DateUtil;
import com.commons.dfs.DfsClientManager;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UploadDfsStorage extends AbstractStorage {

    private static Logger logger = LoggerFactory.getLogger(UploadDfsStorage.class);

    private String dfsKey = null;//上传dfs 的名称
    private DfsClientManager dfsClientManager;//dfs管理端

    public void setDfsKey(String dfsKey) {
        this.dfsKey = dfsKey;
    }

    public void setDfsClientManager(DfsClientManager dfsClientManager) {
        this.dfsClientManager = dfsClientManager;
    }

    /**
     * 上传文件
     */
    public Map<String, List<UploadFile>> uploadFile(HttpServletRequest request) throws ServiceException {
        Map<String, List<UploadFile>> resultmap = null;

        try {
            //文件返回list
            resultmap = new HashMap<>();

            //获取待处理文件组
            List<MultipartFile> todoFileList = todoFiles(request);


            //保存文件
            for (MultipartFile todoFile : todoFileList) {
                String inputName = ((CommonsMultipartFile) todoFile).getFileItem().getFieldName();
                String rawname = todoFile.getOriginalFilename();// 文件原名称 含后缀
                String suffix = rawname.substring(rawname.lastIndexOf(".") + 1);// 文件后缀
                String size = new BigDecimal(todoFile.getSize()).divide(new BigDecimal(1024), 2, RoundingMode.HALF_DOWN).toString();// 文件大小 /1024 = kb  /1024/1024=mb
                String name = DateUtil.getNowTime("yyyyMMddHHmmss") + RandomStringUtils.randomNumeric(6);// 文件转码后名称 含后缀

                UploadFile file = new UploadFile();
                file.setName(name);
                file.setRawname(rawname);
                file.setSize(size);
                file.setSuffix(suffix);
                file.setFullPath("dfs not have fullpath");


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
                    String dfsPath = dfsClientManager.getOpts(dfsKey).createFile(todoFile.getBytes(), suffix);
                    file.setFilepath(dfsPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                logger.info(file.toString());
                if (resultmap.get(inputName) == null) {
                    resultmap.put(inputName, new LinkedList<UploadFile>());
                }
                resultmap.get(inputName).add(file);
            }
        } catch (IllegalStateException e) {
            throw new ServiceException(ResultCode.ERROR_FILE);
        }
        return resultmap;
    }


}
