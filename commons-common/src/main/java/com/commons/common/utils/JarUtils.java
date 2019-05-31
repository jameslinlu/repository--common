package com.commons.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Copyright @ 2015 yuzhouwan.com
 * All right reserved.
 * Function: Jar Util
 *
 * @author Benedict Jin
 * @since 2016/4/20 0030
 */
public class JarUtils {

    private static final Logger _log = LoggerFactory.getLogger(JarUtils.class);
    private static String JAR_PATH;
    private static volatile Properties properties;

    private static final String LIB_PATH = DirUtils.getLibPathInWebApp();

    /**
     * 从 Jar内部 遍历配置文件，并加载
     *
     * @throws IOException
     * @throws URISyntaxException
     */
    private static void scanDirWithinJar(String propPath) throws IOException, URISyntaxException {
        //如果是 webApp，这里需要是改为 ".." + JAR_PATH；否则，直接用 JAR_PATH
        URL sourceUrl = JarUtils.class.getClassLoader().getResource(".." + JAR_PATH);
        if (sourceUrl == null)
            return;
        ZipInputStream zip = new ZipInputStream(sourceUrl.toURI().toURL().openStream());
        if (zip == null)
            throw new RuntimeException(JAR_PATH.concat(" is not exist!!"));

        try {
            while (true) {

                ZipEntry e = zip.getNextEntry();
                if (e == null)
                    break;
                String name = e.getName();
                if (name.startsWith(propPath)) {
                    if (StringUtil.isEmpty(StringUtil.cutStartStr(name, propPath)))
                        continue;
//                    _log.debug(name);
                    properties.load(JarUtils.class.getResourceAsStream("/".concat(name)));
                }
            }
        } finally {
            if (zip != null)
                try {
                    zip.close();
                } catch (IOException e) {
                    _log.error(e.getMessage());
                }
        }
    }

    public static Properties getProperties(String propPath) {
        if (properties == null)
            synchronized (JarUtils.class) {
                if (properties == null)
                    init(propPath);
            }
        return properties;
    }

    private static void init(String propPath) {
        try {
            properties = new Properties();
            List<String> jarPaths = DirUtils.findPath(LIB_PATH, ".jar", false, "lib");
            if (jarPaths == null || jarPaths.size() <= 0)
                return;
            for (String jarFile : jarPaths) {
                jarFile = jarFile.substring(1);
                //如果是 webApp，这里需要改为 WEB-INF; 否则是 target
                jarPaths = DirUtils.findPath(LIB_PATH, jarFile, false, "WEB-INF");
                if (jarPaths == null || jarPaths.size() <= 0)
                    continue;
                JAR_PATH = jarPaths.get(0);
                scanDirWithinJar(propPath.concat("/"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}