package com.commons.common.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C)
 * Base64ParamUtils
 * Author: jameslinlu
 */
public class Base64ParamUtils {

    private static final Logger logger = LoggerFactory.getLogger(Base64ParamUtils.class);

    /**
     * 解密后 按url规则抓取参数
     *
     * @param base64Param
     * @return
     */
    public static Map<String, Object> getMapValues(String base64Param) {
        String decodeParams = null;
        try {
            decodeParams = new String(Base64Utils.decode(base64Param.getBytes("UTF-8")), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("Base 64 Decode Utf-8 Fail", e);
        }
        logger.debug("Base64 Receiver Params : {}", decodeParams);
        String[] paramsKv = decodeParams.split("&");
        String key = null;
        String val = null;
        Map<String, Object> results = new HashMap<>();
        for (String kv : paramsKv) {
            if (kv.equals("")) {
                continue;
            }
            try {
                key = URLDecoder.decode(kv.substring(0, kv.indexOf("=")), "UTF-8");
                val = URLDecoder.decode(kv.substring(kv.indexOf("=") + 1, kv.length()), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                logger.error("Url Decode Utf-8 Fail", e);
                continue;
            }
            if (results.get(key) != null) {//为数组参数
                String[] arrays = null;
                if (results.get(key).getClass().isAssignableFrom(String[].class)) {
                    arrays = (String[]) results.get(key);
                    arrays = ArrayUtils.add(arrays, val);
                }
                if (results.get(key).getClass().isAssignableFrom(String.class)) {
                    arrays = new String[]{results.get(key).toString(), val};
                }
                results.put(key, arrays);
                continue;
            }
            results.put(key, val);
        }
        return results;
    }

}
