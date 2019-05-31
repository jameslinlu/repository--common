package com.commons.common.utils.webdriver;

import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


/**
 * Copyright (C)
 * CommonWebDriverFactory
 * Author: jameslinlu
 */
public class CommonWebDriverFactory {

    /**
     * 按需获取浏览器
     *
     * @param browser org.openqa.selenium.remote.BrowserType
     * @return
     */
    public static RemoteWebDriver newBrowser(String browser) {
        RemoteWebDriver driver;
        DesiredCapabilities capability;
        switch (browser) {
            case BrowserType.FIREFOX:
                capability = DesiredCapabilities.firefox();
                capability.setJavascriptEnabled(true);
                driver = new FirefoxDriver(capability);
                break;
            case BrowserType.IE:
                capability = DesiredCapabilities.internetExplorer();
                capability.setJavascriptEnabled(true);
                driver = new InternetExplorerDriver(capability);
                break;
            case BrowserType.CHROME:
                capability = DesiredCapabilities.chrome();
                capability.setJavascriptEnabled(true);
                driver = new ChromeDriver(capability);
                break;
            case BrowserType.PHANTOMJS:
                //ignore ssl
                ArrayList<String> cliArgsCap = new ArrayList<>();
                cliArgsCap.add("--web-security=false");
                cliArgsCap.add("--ssl-protocol=any");
                cliArgsCap.add("--ignore-ssl-errors=true");

                capability = DesiredCapabilities.phantomjs();
                capability.setJavascriptEnabled(true);
                capability.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
                capability.setCapability("phantomjs.page.settings.resourceTimeout", 60 * 1000);
                capability.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
                driver = new PhantomJSDriver(capability);
                break;
            default:
                throw new IllegalStateException("Unsupported BrowserType " + browser);
        }

        driver.manage()
                .timeouts()
                .implicitlyWait(60, TimeUnit.SECONDS)
                .pageLoadTimeout(60, TimeUnit.SECONDS)
                .setScriptTimeout(60, TimeUnit.SECONDS);

        driver.manage().deleteAllCookies();
        driver.manage().window().maximize();

        return driver;
    }


}
