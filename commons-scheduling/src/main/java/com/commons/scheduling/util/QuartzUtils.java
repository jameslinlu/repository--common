package com.commons.scheduling.util;

import com.alibaba.fastjson.JSON;
import com.commons.common.utils.SecurityUtils;
import com.commons.scheduling.quartz.SpringMethodInvokingJobDetailFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;

/**
 * Copyright (C)
 * QuartzUtils
 * Author: jameslinlu
 */
public class QuartzUtils {

    public static BeanDefinitionBuilder builderJobTrigger(Object jobDetail, String triggerCronExpression, String jobGroup) {
        BeanDefinitionBuilder cronTriggerBuilder = BeanDefinitionBuilder.genericBeanDefinition(CronTriggerFactoryBean.class);
        cronTriggerBuilder.addPropertyValue("jobDetail", jobDetail);
        cronTriggerBuilder.addPropertyValue("cronExpression", triggerCronExpression);
        cronTriggerBuilder.addPropertyValue("group", jobGroup);
        return cronTriggerBuilder;
    }

    public static BeanDefinitionBuilder builderJobDetail(String jobName, String jobGroup, String beanName, String method, Object[] arguments,Boolean concurrent,Boolean durability,Boolean recovery) {
        BeanDefinitionBuilder jobDetailBuilder = BeanDefinitionBuilder.genericBeanDefinition(SpringMethodInvokingJobDetailFactoryBean.class);
        jobDetailBuilder.addPropertyValue("targetBeanName", beanName);
        jobDetailBuilder.addPropertyValue("targetMethod", method);
        jobDetailBuilder.addPropertyValue("targetArguments", arguments);
        jobDetailBuilder.addPropertyValue("name", jobName);
        jobDetailBuilder.addPropertyValue("group", jobGroup);
        jobDetailBuilder.addPropertyValue("concurrent",concurrent);
        jobDetailBuilder.addPropertyValue("durability",durability);
        jobDetailBuilder.addPropertyValue("requestsRecovery",recovery);
        return jobDetailBuilder;
    }

    public static String getBeanName(String jobClassString) throws Exception {
        Class jobClass = Class.forName(jobClassString);
        //用于设置持久化bean名称
        return jobClass.getSimpleName().substring(0, 1).toLowerCase() + jobClass.getSimpleName().substring(1);
    }

    public static String getJobName(String jobClassString, String jobMethod, Object[] jobArguments) throws Exception {
        Class jobClass = Class.forName(jobClassString);
        //用于 组装 任务的名称
        String jobSignature = SecurityUtils.md5(JSON.toJSONString(jobArguments));
        String classMethodName = jobClass.getSimpleName() + "_" + jobMethod.substring(0, 1).toUpperCase() + jobMethod.substring(1);
        String jobBeanName = classMethodName + "_" + jobSignature + "_Job";
        return jobBeanName;
    }

    public static String getJobTriggerName(String jobClassString, String jobMethod, Object[] jobArguments) throws Exception {
        Class jobClass = Class.forName(jobClassString);
        String jobSignature = SecurityUtils.md5(JSON.toJSONString(jobArguments));
        String classMethodName = jobClass.getSimpleName() + "_" + jobMethod.substring(0, 1).toUpperCase() + jobMethod.substring(1);
        String triggerName = classMethodName + "_" + jobSignature + "_CronTrigger";
        return triggerName;
    }

}
