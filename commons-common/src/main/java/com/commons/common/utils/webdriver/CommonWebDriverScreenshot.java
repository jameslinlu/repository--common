package com.commons.common.utils.webdriver;

import com.commons.common.utils.StringUtil;
import com.commons.common.utils.Threads;
import com.commons.metadata.exception.ServiceException;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.remote.BrowserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * Copyright (C)
 * CommonWebDriverScreenshot
 * Author: jameslinlu
 */
public class CommonWebDriverScreenshot {

    private static Logger logger = LoggerFactory.getLogger(CommonWebDriverScreenshot.class);

//    public static void main(String[] args) throws Exception {
//        List<Cookie> cookies = new ArrayList<>();
//        Cookie cookie = new Cookie("TOKEN_PORTAL", "12_7a314f4d605217f132e201258f4054b265bdd88750a746dcfe5d20d013796234c85824b5d7ccf0f0e18824351d703e82ad8732c86415fbe1a45d4c1065511fc8", "https://220.248.112.35:58088".replaceAll("http://", "").replaceAll("https://", ""), "/", new Date(System.currentTimeMillis() + 1000 * 60), false, false);
//        cookies.add(cookie);
//        byte[] screen = CommonWebDriverScreenshot.screenshotPhantomJS("https://220.248.112.35:58088/#/download_report?reportId=20", cookies, "Render Completed");
//        byte[] bytes = PdfUtils.imagePdf(screen);
//        IOUtils.write(bytes, new FileOutputStream(new File("D:\\" + RandomUtils.nextInt(0, 999) + ".pdf")));
//    }

    /**
     * 根据URL截屏
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static byte[] screenshotPhantomJS(String url) throws Exception {
        return screenshot(BrowserType.PHANTOMJS, url, null, byte[].class);
    }

    public static byte[] screenshotPhantomJS(String url, List<Cookie> cookies, String finishConsoleFlag) throws Exception {
        return screenshot(BrowserType.PHANTOMJS, url, cookies, byte[].class, finishConsoleFlag);
    }


    /**
     * 获取浏览器屏幕快照
     * 此种针对静态页面  提供对第三方页面使用
     *
     * @param browserType 浏览器类型
     * @param url         访问地址
     * @param clazz       返回值类型 支持 File String Byte
     * @param <T>         返回转成相应的Class
     * @return
     * @throws ServiceException
     */
    public static <T> T screenshot(String browserType, String url, List<Cookie> cookies, Class clazz) throws Exception {
        return screenshot(browserType, url, cookies, clazz, null);
    }

    public static <T> T screenshot(String browserType, String url, List<Cookie> cookies, Class clazz, String finishConsoleFlag) throws Exception {
        //for local testing
//        System.setProperty("phantomjs.binary.path", "D:\\Develop\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");

        OutputType outputType = null;
        if (clazz.isAssignableFrom(File.class)) {
            outputType = OutputType.FILE;
        }
        if (clazz.isAssignableFrom(String.class)) {
            outputType = OutputType.BASE64;
        }
        if (clazz.isAssignableFrom(byte[].class)) {
            outputType = OutputType.BYTES;
        }
        WebDriver driver = CommonWebDriverFactory.newBrowser(browserType);
        if (cookies != null && !cookies.isEmpty()) {
            for (Cookie cookie : cookies) {
                driver.manage().addCookie(cookie);
            }
        }
        driver.get(url);

        if (!StringUtil.isEmpty(finishConsoleFlag)) {
            //延迟判断浏览器Console约定加载完成标示位
            int sleepStep = 100;
            float sleepTotal = 0;
            boolean rendered = false;
            while (true) {
                Threads.sleep(sleepStep);
                logger.debug("Wait Page Finish Sleep {} ms", sleepStep);
                sleepTotal += sleepStep;
                LogEntries logs = driver.manage().logs().get("browser");
                List<LogEntry> logEntries = logs.getAll();
                for (LogEntry log : logEntries) {
                    if (log.getMessage().contains(finishConsoleFlag)) {
                        logger.debug(" Page Finish Spend {} ms", sleepTotal);
                        rendered = true;
                        break;
                    }
                }
                if (rendered || sleepTotal > 1000 * 30) {
                    break;
                }
            }
            if (!rendered) {
                logger.error("Page Not Finish,No Complete Render {} {} {}", url, cookies, finishConsoleFlag);
            }
        }
        T result = (T) ((TakesScreenshot) driver).getScreenshotAs(outputType);
        driver.close();
        driver.quit();
        return result;
    }


}
