package com.commons.metadata.model.schedule;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright (C)
 * ScheduleJob
 * Author: jameslinlu
 */
public class ScheduleJob implements Serializable {
    //集群实例
    private String instance;
    //任务名称
    private String name;
    //任务组
    private String group;
    //表达式
    private String cron;
    //状态
    private String state;
    //备注
    private String comment;
    //任务开始时间
    private Date startTime;
    //任务结束时间
    private Date endTime;
    //下次触发时间
    private Date nextFireTime;
    //上次触发时间
    private Date previousFireTime;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getNextFireTime() {
        return nextFireTime;
    }

    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public Date getPreviousFireTime() {
        return previousFireTime;
    }

    public void setPreviousFireTime(Date previousFireTime) {
        this.previousFireTime = previousFireTime;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
