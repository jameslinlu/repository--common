package com.commons.cache.util;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.support.ApplicationObjectSupport;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 锁工具 目前是Redisson实现
 */
public class LockUtil extends ApplicationObjectSupport {

    /**
     * 静态变量
     */
    private static LockUtil holder;//=new LockUtil();

    private LockUtil() {
    }

    /**
     * 初始化从spring获取bean
     */
    @PostConstruct
    private void initialize() {
        holder = getApplicationContext().getBean(LockUtil.class);
    }

    /**
     * 单例
     */
    public static LockUtil getInstance() {
        return holder;
    }

    public RLock getLock(String key) {
        return getClient().getLock(key);
    }

    public RLock getFLock(String key) {
        return getClient().getFairLock(key);
    }

    public RReadWriteLock getRwLock(String key) {
        return getClient().getReadWriteLock(key);
    }


    RedissonClient client = null;

    /**
     * 按管理的cache key取
     *
     * @return
     */
    public RedissonClient getClient() {
        Object obj = CacheUtil.getInstance().getCache().getNativeCache();
        if (!(obj instanceof RedissonClient)) {
            throw new RuntimeException("Not Support Other Lock Client,Please Change to Redisson");
        }
        return CacheUtil.getInstance().getCache().getNativeCache();
//        Config c=new Config();
//        c.useSingleServer().setAddress("192.168.130.5:6379");
//        client=Redisson.create(c);
//        return client;
    }

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                test("aa");
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                test("aa1");
            }
        }).start();
//        try {
//            Thread.sleep(8*1000);
//        } catch (InterruptedException e) {
//        }
//        test("aa1");
    }

    public static void test(String key) {
        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(1 * 500);
            } catch (InterruptedException e) {
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    RLock lock = LockUtil.getInstance().getLock(key);
//                  System.out.println(Thread.currentThread().getName()+" isLock "+lock.isLocked());
                    boolean locked = false;
                    try {
                        locked = lock.tryLock(1, 8, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!locked) {
                        System.out.println(key + "--" + Thread.currentThread().getName() + " 放弃Lock " + key);
                        return;
                    }
//                    System.out.println(Thread.currentThread().getName()+" TryLock "+lock.tryLock());
//                    lock.lock();
                    System.out.println(key + "--" + Thread.currentThread().getName() + " 获得Lock " + key);
                    try {
                        Thread.sleep(1 * 500);
                    } catch (InterruptedException e) {
                    }
//                    lock.unlock();
                    System.out.println(key + "--" + Thread.currentThread().getName() + " 释放Lock " + key);
                }
            }).start();
        }
    }
}
