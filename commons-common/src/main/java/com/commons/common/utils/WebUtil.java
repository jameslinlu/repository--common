package com.commons.common.utils;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * web层 req res msg 管理
 */
public class WebUtil {


    private static final Logger logger = LoggerFactory.getLogger(WebUtil.class);

    public static ServletRequestAttributes getServletRequestAttributes() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes());
    }

    public static HttpServletRequest getRequest() {
        return getServletRequestAttributes().getRequest();
    }

    public static HttpServletResponse getResponse() {
        return getServletRequestAttributes().getResponse();
    }

    public static HttpSession getSession() {
        return getServletRequestAttributes().getRequest().getSession(true);
    }

    public static Cookie[] getCookies() {
        return CookieUtils.getCookies();
    }

    public static Cookie getCookie(String name) {
        return CookieUtils.getCookie(name);
    }

    public static String getCookieValue(String name) {
        return CookieUtils.getCookieValue(name);
    }

    public static void removeCookie(String name) {
        CookieUtils.removeCookie(name);
    }


    /**
     * response输出JSON对象
     *
     * @param responseBody
     */
    public static void responseJSON(Object responseBody) {
        try {

            //hook 针对全部使用普通输出方式的json在输出时判断系统配置参数若设置了则进行转换
            String enabled = PropUtil.get("response.encode.enable");
            if (enabled != null && Boolean.valueOf(enabled)) {
                String key = PropUtil.get("response.encode.field");
                responseBody = JSONUtils.bytesValueMap(key, responseBody);
            }

            HttpServletResponse response = getResponse();
            response.setHeader("Cache-Control", "no-cache");
            response.setContentType("text/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print(JSON.toJSONString(responseBody));
            response.getWriter().flush();
        } catch (Exception e) {
            logger.error("response JSON error", e);
        }
    }

    /**
     * map的value 转换为string 再转为 bytes, fast json自动将bytes进行base64
     *
     * @param object
     */
    public static void responseBytesValueJSON(String key, Object object) {
        try {
            //使用fastjson作为默认输出 内部使用base64编码
            responseJSON(JSONUtils.bytesValueMap(key, object));
        } catch (Exception e) {
            logger.error("response JSON error", e);
        }
    }

    /**
     * 设置SESSION属性
     */
    public static void setSessionAttribute(String name, Object value) {
        getSession().setAttribute(name, value);
    }


    /**
     * 获取访问IP地址
     */
    public static String getRemoteAddress() {
        HttpServletRequest request = getRequest();
        return getRemoteAddress(request);
    }

    public static String getRemoteAddress(HttpServletRequest request) {
        if (null == request) {
            return null;
        }
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 获取session 属性
     */
    public static <T> T getSessionAttribute(String name) {
        return (T) getSession().getAttribute(name);
    }

    /**
     * 获取Web容器访问上下文路径
     * /test
     *
     * @return context path
     */
    public static String getContextPath() {
        return getRequest().getContextPath();
    }

    /**
     * 获取当前请求的域名
     * URL：http://wechat.cloudguarder.com/cgptest/api/wx/authorize
     * URI:                               /cgptest/api/wx/authorize
     *
     * @return http://wechat.cloudguarder.com
     */
    public static String getRequestRoot() {
        return getRequestURL().replaceAll(getRequest().getRequestURI(), "");
    }

    /**
     * 获取当前请求地址
     *
     * @return url
     */
    public static String getRequestURL() {
        return getRequest().getRequestURL().toString();
    }

    /**
     * 获取应用根目录
     * D:\Tomcat 6.0\webapps\test\
     */
    public static String getWebRoot() {
        return ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/");
    }

    /**
     * 获取应用根目录+指定目录地址
     * D:\Tomcat 6.0\webapps\test\  + path
     */
    public static String getWebRoot(String path) {
        return getWebRoot() + path;
    }

}
