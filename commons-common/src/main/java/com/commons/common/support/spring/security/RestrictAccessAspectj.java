package com.commons.common.support.spring.security;

import com.commons.cache.util.CacheUtil;
import com.commons.common.generic.model.ResponseErrorMessage;
import com.commons.common.generic.model.ResponseMessage;
import com.commons.common.utils.WebUtil;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 访问失败超限拦截器
 * Copyright (C)
 * Logger
 * Author: jameslinlu
 */
public class RestrictAccessAspectj {

    private static final Logger logger = LoggerFactory.getLogger(RestrictAccessAspectj.class);

    //例如 60秒内失败10次 间隔2分钟
    //访问限制次数 次数
    private Integer accessTimes;
    //访问限制间隔 秒
    private Integer accessIntervalSeconds;
    //拒绝时间 分钟
    private Integer denySeconds;

    public void setAccessTimes(Integer accessTimes) {
        this.accessTimes = accessTimes;
    }

    public void setAccessIntervalSeconds(Integer accessIntervalSeconds) {
        this.accessIntervalSeconds = accessIntervalSeconds;
    }

    public void setDenySeconds(Integer denySeconds) {
        this.denySeconds = denySeconds;
    }

    private String resetAccess(String ip) {
        String val = "1," + new Date().getTime();
        CacheUtil.getInstance().getCache().set("ACCESS:" + ip, val, accessIntervalSeconds);
        return val;
    }

    /**
     * 检查访问限制
     */
    private ResponseErrorMessage checkAccessLimit(String ip, String[] valArrays) {
        //截至时间 = 开始时间 + 持续秒
        String times = valArrays[0];
        //已锁定则跳过
        if (times.equals("-1")) {
            ResponseErrorMessage lock = new ResponseErrorMessage(new ServiceException(ResultCode.ERROR_ACCESS_LIMIT));
            return lock;
        }
        Long date = Long.valueOf(valArrays[1]);
        Date endLimit = new Date(date + accessIntervalSeconds * 1000);
        //时间范围超过重置
        if (System.currentTimeMillis() > endLimit.getTime()) {
            resetAccess(ip);
        }
        //有效时间内超限则锁定
        if (System.currentTimeMillis() < endLimit.getTime() && Integer.valueOf(times) >= accessTimes) {
            CacheUtil.getInstance().getCache().set("ACCESS:" + ip, "-1", denySeconds);
            ResponseErrorMessage lock = new ResponseErrorMessage(new ServiceException(ResultCode.ERROR_ACCESS_LIMIT));
            return lock;
        }
        return null;
    }

    /**
     * 递增ip访问次数存于redis
     *
     * @param ip
     */
    private void incrementAccessTimes(String ip) {
        String val = CacheUtil.getInstance().getCache().get("ACCESS:" + ip);
        if (val != null && !val.equals("-1")) {
            String[] valArrays = val.split(",");
            String ipTimes = valArrays[0];
            int times = Integer.valueOf(ipTimes);
            times++;
            CacheUtil.getInstance().getCache().set("ACCESS:" + ip, times + "," + valArrays[1], accessIntervalSeconds);
        }
    }

    /**
     * 日志AOP处理
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        Object returnValue = null;
        try {
            String ip = WebUtil.getRemoteAddress();
            String val = CacheUtil.getInstance().getCache().get("ACCESS:" + ip);
            //记录访问ip 次数为0
            if (val == null) {
                val = resetAccess(ip);
            }
            String[] valArrays = val.split(",");
            //检查访问次数是否超限
            ResponseMessage msg = checkAccessLimit(ip, valArrays);
            if (msg != null) {
                return msg;
            }

            returnValue = pjp.proceed();
            return returnValue;
        } catch (Throwable ex) {
            throw ex;
        } finally {
            if (returnValue instanceof ResponseMessage) {
                boolean succ = ((ResponseMessage) returnValue).isSuccess();
                if (!succ) {
                    //非成功请求增加计数
                    String ip = WebUtil.getRemoteAddress();
                    incrementAccessTimes(ip);
                }
            }
        }

    }


}
