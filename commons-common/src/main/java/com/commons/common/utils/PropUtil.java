package com.commons.common.utils;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropUtil {

    public static String FOLDER = "prop";
    private static Properties properties;
    private static Map<String, Properties> domainProperties;

    static {
        if (properties == null) {
            try {
                properties = new Properties();
                Properties files = PropertiesLoaderUtils.loadAllProperties(FOLDER);
                for (Object key : files.keySet()) {
                    PropertiesLoaderUtils.fillProperties(properties, new FileSystemResource(ResourceUtils.getFile(ClassUtils.getDefaultClassLoader().getResource(FOLDER + "/" + key.toString()))));
                }
                Properties propertiesInJar = JarUtils.getProperties(FOLDER);
                if (propertiesInJar != null && propertiesInJar.size() > 0) {
                    for (Object propertiesKey : propertiesInJar.keySet()) {
                        properties.put(propertiesKey, propertiesInJar.getProperty(propertiesKey.toString()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (domainProperties == null) {
            domainProperties = new Hashtable<>();
        }
    }

    public static void put(String key, String val, String domain) {
        Properties props = domainProperties.get(domain);
        if (props == null) {
            domainProperties.put(domain, new Properties());
        }
        domainProperties.get(domain).put(key, val);
    }

    public static String get(String key, String domain) {
        Properties props = domainProperties.get(domain);
        if (props == null) {
            return null;
        }
        return domainProperties.get(domain).get(key).toString();
    }


    public static Map<String, String> getPrefix(String keyPrefix,String domain) {
        return new HashMap<>((Map) getPrefixProperties(keyPrefix, null, null,domain));
    }
    public static Map<String, String> getPrefix(String keyPrefix, String replace, String to,String domain) {
        return new HashMap<>((Map) getPrefixProperties(keyPrefix, replace, to,domain));
    }

    public static Properties getPrefixProperties(String keyPrefix, String replace, String to,String domain) {
        Properties props = domainProperties.get(domain);
        if(null==props ){
            return  new Properties();
        }
        return  replaceProperties(keyPrefix,replace,to,props);
    }

    public static void delete(String key, String domain) {
        Properties props = domainProperties.get(domain);
        if (props == null) {
            return;
        }
        domainProperties.get(domain).remove(key);
    }

    public static void delete(String key) {
        properties.remove(key);
    }

    public static void put(String key, String val) {
        properties.put(key, val);
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static String get(String key, Object defaultVal) {
        return properties.getProperty(key, defaultVal == null ? null : String.valueOf(defaultVal));
    }

    public static Map<String, String> getPrefix(String keyPrefix) {
        return new HashMap<>((Map) getPrefixProperties(keyPrefix, null, null));
    }

    public static Map<String, String> getPrefix(String keyPrefix, String replace, String to) {
        return new HashMap<>((Map) getPrefixProperties(keyPrefix, replace, to));
    }

    public static Properties getPrefixProperties(String keyPrefix, String replace, String to) {
        return replaceProperties(keyPrefix,replace,to,properties);
    }

    private static Properties replaceProperties( String keyPrefix,String replace, String to,Properties prop){
        Properties result = new Properties();
        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith(keyPrefix + ".")) {
                String returnKey = key;
                if (replace != null && to != null) {
                    //返回key替换
                    returnKey = returnKey.replaceAll(replace, to);
                }
                result.put(returnKey, get(key));
            }
        }
        return result;
    }

    /**
     * 从keystring中解析patten得到key再从配置文件中获取
     *
     * @param keyString  ${proxy.platform.ip}
     * @param keyPatten  default \$\{(.+)\}
     * @param defaultVal null
     * @return
     */
    public static String getPlaceholder(String keyString, String keyPatten, String defaultVal) {
        if (keyPatten == null) {
            keyPatten = "\\$\\{(.+)\\}";
        }
        Pattern p = Pattern.compile(keyPatten);
        Matcher m = p.matcher(keyString);
        if (m.find()) {
            return PropUtil.get(m.group(1).trim());
        } else {
            return defaultVal;
        }
    }

}
