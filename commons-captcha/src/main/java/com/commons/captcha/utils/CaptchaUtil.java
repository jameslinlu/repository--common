package com.commons.captcha.utils;

import com.commons.cache.util.CacheUtil;
import com.commons.captcha.exception.CaptchaResultCode;
import com.commons.captcha.service.Captcha;
import com.commons.captcha.service.ConfigurableCaptchaService;
import com.commons.common.utils.Base64ParamUtils;
import com.commons.common.utils.StringUtil;
import com.commons.common.utils.WebUtil;
import com.commons.metadata.exception.ServiceException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C)
 * CaptchaUtil
 * Author: jameslinlu
 */
public class CaptchaUtil {

    private static ConfigurableCaptchaService captchaService;

    private static String CAPTCHA_TOKEN_KEY = "_ck";
    private static String CAPTCHA_TOKEN = "CAPTCHA_TOKEN";

    static {
        captchaService = new ConfigurableCaptchaService();
        captchaService.setWidth(145);
        captchaService.setHeight(35);
        captchaService.getWordFactory().setMaxLength(5);
        captchaService.getWordFactory().setMinLength(5);
        captchaService.getTextRenderer().setBottomMargin(5);
        captchaService.getTextRenderer().setTopMargin(5);
        captchaService.getTextRenderer().setLeftMargin(10);
        captchaService.getTextRenderer().setRightMargin(10);
        captchaService.getFontFactory().setMaxSize(27);
        captchaService.getFontFactory().setMinSize(27);
    }


    /**
     * 获取验证码
     * 前端访问获取验证时指定 校验key  分布式情况下选用此方式
     */
    public static Map<String, Object> generate() {
        String cacheKey = getCacheKey();
//        Cookie[] cookies = WebUtil.getRequest().getCookies();
//        if (cookies != null) {
//            //删除 旧的cookie &  cache
//            for (Cookie cookie : cookies) {
//                if (cookie.getName().equals(CAPTCHA_TOKEN)) {
//                    String cacheKey = cookie.getValue();
//                    CacheUtil.getInstance().getCache().del(cacheKey);
//                }
//            }
//        }
//        String cacheKey = SecurityUtils.generateSalt(10) ;
        Integer expireSecond = 300;

//        Cookie cookie = new Cookie(CAPTCHA_TOKEN, cacheKey);
//        cookie.setMaxAge(expireSecond);//验证码300秒 有效
//        WebUtil.getResponse().addCookie(cookie);

        Captcha captcha = captchaService.getCaptcha();
        CacheUtil.getInstance().getCache().set(cacheKey, captcha.getChallenge(), expireSecond);

        Map<String, Object> result = new HashMap<>();
        result.put("token", cacheKey);
        result.put("captcha", captcha);
        return result;
    }

    private static String getCacheKey(){
        String cacheKey = WebUtil.getRequest().getParameter(CAPTCHA_TOKEN_KEY);
        if (cacheKey == null && WebUtil.getRequest().getParameter("request") != null) {
            Object result = Base64ParamUtils.getMapValues(WebUtil.getRequest().getParameter("request")).get(CAPTCHA_TOKEN_KEY);
            if (result != null) {
                cacheKey = result.toString();
            }
        }
        if (cacheKey == null) {
            cacheKey = WebUtil.getRequest().getSession().getId();
        }
        return cacheKey;
    }

    /**
     * 验证验证码
     * 前端访问验证时指定 校验key 分布式情况下选用此方式
     */
    public static void valid(String captchaCode) throws ServiceException {
        String cacheKey = getCacheKey();
//        Cookie[] cookies = WebUtil.getRequest().getCookies();
        boolean flag = false;
//        if (cookies != null) {
//            //删除 旧的cookie &  cache
//            for (Cookie cookie : cookies) {
//                if (cookie.getName().equals(CAPTCHA_TOKEN)) {
//                    String cacheKey = cookie.getValue();
        String captcha = CacheUtil.getInstance().getCache().get(cacheKey);
        if (!StringUtil.isEmpty(captcha) && captcha.equals(captchaCode)) {
            flag = true;
            CacheUtil.getInstance().getCache().del(cacheKey);
        }
//                }
//            }
//        }
        if (!flag) {
            throw new ServiceException(CaptchaResultCode.CAPTCHA_VALID_FAIL);
        }
    }

    /**
     * 显示验证码
     */
    public static void show(BufferedImage image) throws ServiceException {
        try {
            ImageIO.write(image, "png", WebUtil.getResponse().getOutputStream());
        } catch (IOException e) {
            throw new ServiceException(CaptchaResultCode.CAPTCHA_GENERATE_FAIL);
        }
    }

    public static void generateAndShow() throws ServiceException {
        Map<String, Object> result = generate();
        show(((Captcha) result.get("captcha")).getImage());
    }
}
