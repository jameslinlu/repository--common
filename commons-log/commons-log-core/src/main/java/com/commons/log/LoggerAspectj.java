package com.commons.log;

import com.alibaba.fastjson.JSON;
import com.commons.common.generic.model.ResponseMessage;
import com.commons.common.utils.Exceptions;
import com.commons.common.utils.Reflections;
import com.commons.common.utils.StringUtil;
import com.commons.common.utils.WebUtil;
import com.commons.log.filter.ILoggerFilter;
import com.commons.log.output.ILoggerOutput;
import com.commons.log.output.impl.DefaultLoggerOutput;
import com.commons.log.utils.LoggerContextUtil;
import com.commons.metadata.model.log.AccessLog;
import com.commons.metadata.model.log.annotation.Log;
import com.commons.metadata.model.log.enums.LoggerLevel;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.util.*;

/**
 * Copyright (C)
 * Logger
 * Author: jameslinlu
 */
public class LoggerAspectj {

    private static final Logger logger = LoggerFactory.getLogger(LoggerAspectj.class);

    private static final String COLON = ":";

    //区分日志域
    private String domain;
    //默认智能省略
    private boolean smartEllipsis = true;
    //记录过程日志级别
    private LoggerLevel level;
    //日志拦截
    private ILoggerFilter filter;

    private ILoggerOutput loggerOutput = new DefaultLoggerOutput();

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setSmartEllipsis(boolean smartEllipsis) {
        this.smartEllipsis = smartEllipsis;
    }

    public void setLevel(String level) {
        this.level = LoggerLevel.getEnum(level);
    }

    public void setFilter(ILoggerFilter filter) {
        this.filter = filter;
    }

    /**
     * 前置日志处理
     *
     * @param log
     * @param pjp
     */
    public void beforeLog(AccessLog log, ProceedingJoinPoint pjp) {

        log.setDomain(domain);
        log.setLevel(level.toString());
        log.setBegin(new Date());
        log.setArguments(coverArguments(pjp.getArgs()));
        log.setMethod(pjp.getSignature().toString());

        if (isAccessEntry(pjp)) {
            log.setIp(WebUtil.getRemoteAddress());
            log.setRequest(WebUtil.getRequestURL());
            log.setUserAgent(WebUtil.getRequest().getHeader("user-agent"));
            log.setHttpStatus(WebUtil.getResponse().getStatus());
            log.setReferLink(WebUtil.getRequest().getHeader("referer"));
            log.setRequestLength(WebUtil.getRequest().getContentLength());
            log.setRequestMethod(WebUtil.getRequest().getMethod());
            LoggerContextUtil.reset();
        }
        log.setTraceId(LoggerContextUtil.getTraceId());
        log.setSequenceId(LoggerContextUtil.getSequenceId() + LoggerContextUtil.getSequenceIdLocalAndIncrement());
        if (log.getSequenceId().equals("0.0")) {
            log.setType("ACCESS");
        } else {
            log.setType("TRACE");
        }
        if (filter != null) {
            filter.before(log);
        }
    }

    /**
     * 后置日志处理
     *
     * @param log
     * @param returnValue
     * @param throwable
     */
    public void afterLog(AccessLog log, Object returnValue, Throwable throwable) {
        log.setEnd(new Date());
        log.setSpendMs(log.getEnd().getTime() - log.getBegin().getTime());
        if (returnValue != null) {
            log.setReturnVal(coverReturnValue(returnValue));
        }
        if (throwable != null) {
            log.setException(Exceptions.getStackTraceAsString(throwable));
        }
        if (filter != null) {
            filter.after(log);
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

        AccessLog log = new AccessLog();
        beforeLog(log, pjp);
        Object returnValue = null;
        Throwable throwable = null;
        try {
            returnValue = pjp.proceed();
            return returnValue;
        } catch (Throwable ex) {
            throwable = ex;
            throw ex;
        } finally {
            afterLog(log, returnValue, throwable);
            extractLogDescription(log, pjp, returnValue);
            loggerOutput.out(log);
        }

    }

    public List<String> extractLogProcess(LinkedList<String> fields, Object[] values) {
        List<String> formatValues = new LinkedList<>();
        Object val = null;
        if (fields == null || fields.size() == 0)
            return formatValues;
        for (String arg : fields) {
            if (StringUtil.isEmpty(arg) || values == null) {
                formatValues.add("");
                continue;
            }
            //args:["j:xxx", ...]
            if (arg.contains(COLON)) {
                String[] args = arg.split(COLON);
                Integer index = Integer.valueOf(args[0]);
                if (values.length >= index) {
                    val = values[index];
                }
                if (val != null) {
                    val = Reflections.getFieldValue(val, args[1]);
                }
            } else {
                //args:[i, ...]
                int idx = Integer.valueOf(arg);
                if (values.length >= idx) {
                    val = values[idx];
                }
            }
            formatValues.add(val == null ? "" : val.toString());
        }
        return formatValues;
    }

    /**
     * 增强日志描述
     *
     * @param accessLog
     * @param pjp
     * @param returnValue
     */
    private void extractLogDescription(AccessLog accessLog, ProceedingJoinPoint pjp, Object returnValue) {

        Log logger = ((MethodSignature) pjp.getSignature()).getMethod().getAnnotation(Log.class);
        if (logger == null) {
            return;
        }
        String description = logger.description();
        String extract = logger.extract();
        if (!StringUtil.isEmpty(extract)) {
            LinkedHashMap<String, LinkedList<String>> extractMap = JSON.parseObject(extract, new com.alibaba.fastjson.TypeReference<LinkedHashMap<String, LinkedList<String>>>() {
            });
            List<String> logValues = new LinkedList<>();
            Object[] argsParams = pjp.getArgs();
            Object[] retsParams = null;
            if (returnValue != null && returnValue instanceof Collection) {
                retsParams = ((Collection) returnValue).toArray();
            } else if (returnValue != null) {
                retsParams = new Object[]{returnValue};
            } else {
                retsParams = new Object[]{null};
            }

            LinkedList<String> argList = extractMap.get("args");
            LinkedList<String> retList = extractMap.get("rets");
            logValues.addAll(extractLogProcess(argList, argsParams));
            logValues.addAll(extractLogProcess(retList, retsParams));
            description = String.format(description, logValues.toArray());
        }
        accessLog.setDescription(description);

    }

    public String coverArguments(Object[] args) {
        StringBuffer buff = new StringBuffer();
        if (args != null) {
            for (Object arg : args) {
                if (arg != null &&
                        !(arg instanceof HttpServletRequest || arg instanceof HttpServletResponse) &&
                        !(arg instanceof Closeable) &&
                        !arg.getClass().isArray() &&
                        !arg.getClass().isInterface()) {
                    try {
                        buff.append(JSON.toJSONString(arg)).append("|");
                    } catch (Exception e) {
                        logger.error("Logger Convert Argument Fail", arg);
                    }
                }
            }
        }
        return buff.toString();
    }

    /**
     * 智能转换返回值
     *
     * @param returnValue
     * @return
     */
    public String coverReturnValue(Object returnValue) {
        if (smartEllipsis) {
            if (returnValue instanceof Collection) {
                return "Size:" + ((Collection) returnValue).size();
            } else if (returnValue instanceof Map) {
                return JSON.toJSONString(((Map) returnValue).keySet().toString());
            } else if (returnValue instanceof ResponseMessage) {
                return ((ResponseMessage) returnValue).getCode() + "," + ((ResponseMessage) returnValue).getMessage();
            } else {
                try {
                    String result = JSON.toJSONString(returnValue);
                    if (result.length() > 500) {
                        result = result.substring(0, 480) + "...";
                    }
                    return result;
                } catch (Exception e) {
                    //忽略返回参数转换异常
                    return "COVERT_ERROR";
                }
            }
        } else {
            try {
                return JSON.toJSONString(returnValue);
            } catch (Exception e) {
                //忽略返回参数转换异常
                return "COVERT_ERROR";
            }
        }
    }

    /**
     * 判断前端入口方法
     *
     * @param pjp
     * @return
     */
    public boolean isAccessEntry(ProceedingJoinPoint pjp) {
        return AnnotationUtils.findAnnotation(pjp.getTarget().getClass(), Controller.class) != null;
    }

}
