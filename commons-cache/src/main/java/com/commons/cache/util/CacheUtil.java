package com.commons.cache.util;

import com.commons.cache.ICacheOperator;
import com.commons.cache.manager.CommonCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ApplicationObjectSupport;

import javax.annotation.PostConstruct;

/**
 * 缓存工具 目前是Redis实现
 * <p/>
 * CacheUtil.getInstance().set("stringe1","hello world", 5);
 * Map<String,Object> map=new HashMap<>();
 * map.put("123",456);
 * map.put("321","789");
 * CacheUtil.getInstance().set("map",map);
 * List<User> list=new ArrayList<>();
 * list.add(u);
 * CacheUtil.getInstance().set("list",list);
 * CacheUtil.getInstance().set("obj",u);
 * <p/>
 * System.out.println(CacheUtil.getInstance().get("string"));
 * System.out.println(CacheUtil.getInstance().get("obj",User.class));
 * System.out.println(CacheUtil.getInstance().get("map",Map.class));
 * System.out.println(CacheUtil.getInstance().get("list",User.class));
 */
public class CacheUtil extends ApplicationObjectSupport {

    /**
     * 默认存储前缀
     */
    private String defaultPrefix = "Default";

    /**
     * 静态变量
     */
    private static CacheUtil holder;

    /**
     * spring cache管理类
     */
    @Autowired
    private CommonCacheManager cacheManager;

    private CacheUtil() {
    }

    /**
     * 初始化从spring获取bean
     */
    @PostConstruct
    private void initialize() {
        holder = getApplicationContext().getBean(CacheUtil.class);
    }

    /**
     * 单例
     */
    public static CacheUtil getInstance() {
        return holder;
    }

    /**
     * 取默认Cache
     */
    public ICacheOperator getCache() {
        return getCache(defaultPrefix);
    }

    public ICacheOperator getCache(String prefixKey) {
        return getCache("default", prefixKey);
    }

    /**
     * 按管理的cache key取
     *
     * @param managerKey
     * @param prefixKey
     * @return
     */
    public ICacheOperator getCache(String managerKey, String prefixKey) {
        return cacheManager.getManager(managerKey).getCacheOperator(prefixKey);
    }

    public void setDefaultPrefix(String defaultPrefix) {
        this.defaultPrefix = defaultPrefix;
    }
}
