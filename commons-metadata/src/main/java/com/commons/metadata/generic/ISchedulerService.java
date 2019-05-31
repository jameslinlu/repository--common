package com.commons.metadata.generic;

import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.schedule.ScheduleExecution;
import com.commons.metadata.model.schedule.ScheduleJob;

import java.util.List;

/**
 * Copyright (C)
 * ISchedulerService
 * Author: jameslinlu
 */
public interface ISchedulerService {

    /**
     * 获取全部任务
     *
     * @return
     * @throws ServiceException
     */
    List<ScheduleJob> getScheduleJobs() throws ServiceException;

    /**
     * 获取运行中任务
     *
     * @return
     * @throws ServiceException
     */
    List<ScheduleJob> getScheduleJobsRunning() throws ServiceException;

    /**
     * 检查任务存在  job存在视为trigger存在
     */
    boolean existScheduleJob(ScheduleExecution execution) throws ServiceException;

    /**
     * 暂停任务
     */
    void pauseScheduleJob(ScheduleExecution execution) throws ServiceException;

    /**
     * 恢复任务
     */
    void resumeScheduleJob(ScheduleExecution execution) throws ServiceException;

    /**
     * 删除任务
     */
    void deleteScheduleJob(ScheduleExecution execution) throws ServiceException;

    /**
     * 添加任务
     */
    void addScheduleJob(ScheduleExecution execution) throws ServiceException;

    /**
     * 修改任务
     */
    void updateScheduleJob(ScheduleExecution execution) throws ServiceException;

    /**
     * 触发任务
     */
    void triggerScheduleJob(ScheduleExecution execution) throws ServiceException;

}
