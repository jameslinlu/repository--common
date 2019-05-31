package com.commons.common.utils;


import com.alibaba.fastjson.JSON;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: json array转list的工具类</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2016 zhong-ying.com Inc.
 * All right reserved.</p>
 *
 * @author : 顾庆崴 on 2016/10/24.
 */
public class JSONUtils {

    public static <T> List<T> parseArray(String str, Class<T> clazz) throws ServiceException {
        try {
            return JSON.parseArray(str, clazz);
        } catch (Exception e) {
            throw new ServiceException(ResultCode.ERROR_JSON, e);
        }
    }

    public static Map<String, Object> bytesValueMap(String key, Object obj) throws Exception {
        //fastjson 转换bytes时会自动base64编码
        String responseBody = JSON.toJSONString(obj);
        byte[] bytes = responseBody.getBytes("UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put(key, bytes);
        return result;
    }

    public static void writeBytesValueJSON(String key, Object obj, OutputStream out) throws Exception {
        String responseBody = JSON.toJSONString(bytesValueMap(key, obj));
        byte[] bytes = responseBody.getBytes("UTF-8");
        out.write(bytes);
    }

}
