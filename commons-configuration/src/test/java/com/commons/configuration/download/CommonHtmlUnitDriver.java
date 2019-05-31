package com.commons.configuration.download;

import com.commons.common.utils.Threads;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.attachment.Attachment;
import com.gargoylesoftware.htmlunit.attachment.CollectingAttachmentHandler;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * selenium模拟浏览器打开页面下载
 * 依赖
 * org.seleniumhq.selenium
 * selenium-server
 * 3.0.1
 * Copyright (C)
 * CommonHtmlUnitDriver
 * Author: jameslinlu
 */
public class CommonHtmlUnitDriver extends HtmlUnitDriver {
    CollectingAttachmentHandler attachmentHandler = null;

    public CommonHtmlUnitDriver(boolean enableJavascript) {
        super(enableJavascript);
        attachmentHandler = new CollectingAttachmentHandler();
        this.getWebClient().setAttachmentHandler(attachmentHandler);
    }

    public WebClient getClient() {
        return this.getWebClient();
    }

    public List<Attachment> getCollectedAttachments() {
        return attachmentHandler.getCollectedAttachments();
    }

    public static void main(String[] args) throws Exception {


//        System.setProperty("phantomjs.binary.path", "D:\\Develop\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");
        CommonHtmlUnitDriver driver = new CommonHtmlUnitDriver(true);
        //js防抓取页面 模拟浏览器 获取文件
        driver.get("http://www.cnvd.org.cn/apiTaskOut/download?id=d89aaadd5b2bcb69ca7af93f9f16da29");
        while (driver.getCollectedAttachments().isEmpty()) {
            Threads.sleep(1000);
        }
        driver.quit();
        for (Attachment attachment : driver.getCollectedAttachments()) {
            File file = new File("D:\\download\\" + attachment.getSuggestedFilename());
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream out = new FileOutputStream(file);
            IOUtils.copy(attachment.getPage().getWebResponse().getContentAsStream(), out);
        }
    }
}
