package com.commons.metadata.model.schedule;

import java.io.Serializable;

/**
 * Copyright (C)
 * ScheduleExecution
 * Author: jameslinlu
 */
public class ScheduleExecution implements Serializable {

    private String jobGroup;
    private String jobCronExpression;
    private String jobClass;//XX.class.getName()
    private String jobMethod;
    private Object[] jobArguments;

    //是否并发执行  同一个job按顺序一个个执行
    private boolean concurrent = true;

    //是否持久化  若false并且无trigger关联则被删除
    private boolean durability = true;

    //中断恢复  若非正常退出,持久化任务会在程序启动后 执行错过的任务
    private boolean recovery = true;

    public boolean isConcurrent() {
        return concurrent;
    }

    public void setConcurrent(boolean concurrent) {
        this.concurrent = concurrent;
    }

    public boolean isDurability() {
        return durability;
    }

    public void setDurability(boolean durability) {
        this.durability = durability;
    }

    public boolean isRecovery() {
        return recovery;
    }

    public void setRecovery(boolean recovery) {
        this.recovery = recovery;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobCronExpression() {
        return jobCronExpression;
    }

    public void setJobCronExpression(String jobCronExpression) {
        this.jobCronExpression = jobCronExpression;
    }

    public String getJobClass() {
        return jobClass;
    }

    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }

    public String getJobMethod() {
        return jobMethod;
    }

    public void setJobMethod(String jobMethod) {
        this.jobMethod = jobMethod;
    }

    public Object[] getJobArguments() {
        return jobArguments;
    }

    public void setJobArguments(Object[] jobArguments) {
        this.jobArguments = jobArguments;
    }
}
