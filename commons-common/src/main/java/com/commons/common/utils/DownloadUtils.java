package com.commons.common.utils;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;


public class DownloadUtils {
    public static final String errorMsg = "您下载的文件不存在";

    public static void download(File file, String filename) throws Exception {
        if (!file.exists() || !file.canRead()) {
            WebUtil.responseJSON(errorMsg);
            return;
        }
        download(new FileInputStream(file), filename);
    }

    public static void download(File file) throws Exception {
        download(file, file.getName());
    }

    public static void download(String filepath) throws Exception {
        File file = new File(filepath);
        download(file, file.getName());
    }

    public static void download(String filepath, String filename) throws Exception {
        download(new File(filepath), filename);
    }

    public static void download(byte[] bytes, String filename) throws Exception {
        if (ArrayUtils.isEmpty(bytes)) {
            WebUtil.responseJSON(errorMsg);
            return;
        }
        download(new ByteArrayInputStream(bytes), filename);
    }

    /**
     * 对流文件下载
     */
    public static void download(InputStream inputStream, String filename) throws Exception {
        HttpServletRequest request = WebUtil.getRequest();
        HttpServletResponse response = WebUtil.getResponse();
        String userAgent = request.getHeader("User-Agent");
        boolean isIE = (userAgent != null) && (userAgent.toLowerCase().indexOf("msie") != -1);

        response.reset();
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "must-revalidate, no-transform");
        response.setDateHeader("Expires", 0L);

        response.setContentType("application/x-download");
        response.setContentLength(inputStream.available());

        String displayFilename = filename;//.substring(filename.lastIndexOf("_") + 1);
        displayFilename = displayFilename.replace(" ", "_");
        if (isIE) {
            displayFilename = URLEncoder.encode(displayFilename, "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=\"" + displayFilename + "\"");
        } else {
            displayFilename = new String(displayFilename.getBytes("UTF-8"), "ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + displayFilename);
        }
        BufferedInputStream is = null;
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            is = new BufferedInputStream(inputStream);
            IOUtils.copy(is, os);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

}
