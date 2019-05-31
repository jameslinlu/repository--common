package com.commons.scheduling.quartz;

import com.commons.common.utils.ContextUtil;
import com.commons.common.utils.Reflections;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.schedule.ScheduleExecution;
import com.commons.metadata.model.schedule.ScheduleJob;
import com.commons.metadata.generic.ISchedulerService;
import com.commons.scheduling.util.QuartzUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.ApplicationObjectSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C)
 * QuartzSchedulerService
 * Author: jameslinlu
 */
public class QuartzSchedulerService extends ApplicationObjectSupport implements ISchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(QuartzSchedulerService.class);

    public QuartzSchedulerService() {
    }

    public QuartzSchedulerFactoryBean getQuartzSchedulerFactoryBean() {
        return ContextUtil.getBean(QuartzSchedulerFactoryBean.class);
    }

    public Scheduler getScheduler() {
        return this.getQuartzSchedulerFactoryBean().getScheduler();
    }

    private ScheduleJob buildJobInfo(String instance, JobKey jobKey, Trigger trigger) throws Exception {
        Trigger.TriggerState triggerState = this.getScheduler().getTriggerState(trigger.getKey());
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setInstance(instance);
        scheduleJob.setName(jobKey.getName());
        scheduleJob.setGroup(jobKey.getGroup());
        scheduleJob.setState(triggerState.name());
        scheduleJob.setStartTime(trigger.getStartTime());
        scheduleJob.setEndTime(trigger.getEndTime());
        scheduleJob.setPreviousFireTime(trigger.getPreviousFireTime());
        scheduleJob.setNextFireTime(trigger.getNextFireTime());
        if (trigger instanceof CronTrigger) {
            scheduleJob.setComment(((CronTriggerImpl) trigger).getCronExpression());
        }
        return scheduleJob;
    }

    public List<ScheduleJob> getScheduleJobs() throws ServiceException {
        try {
            String instance = (String) Reflections.getFieldValue(this.getQuartzSchedulerFactoryBean(), "schedulerName");
            List<ScheduleJob> scheduleJobs = new ArrayList<>();
            Set<JobKey> jobKeys = this.getScheduler().getJobKeys(GroupMatcher.<JobKey>anyGroup());
            for (JobKey jobKey : jobKeys) {
                List<? extends Trigger> triggers = this.getScheduler().getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers) {
                    scheduleJobs.add(this.buildJobInfo(instance, jobKey, trigger));
                }
            }
            return scheduleJobs;
        } catch (Exception e) {
            throw new ServiceException(ResultCode.ERROR_SEARCH, e);
        }
    }

    public List<ScheduleJob> getScheduleJobsRunning() throws ServiceException {
        try {
            String instance = (String) Reflections.getFieldValue(this.getQuartzSchedulerFactoryBean(), "schedulerName");
            List<JobExecutionContext> executingJobs = this.getScheduler().getCurrentlyExecutingJobs();
            List<ScheduleJob> scheduleJobs = new ArrayList<>(executingJobs.size());
            for (JobExecutionContext executingJob : executingJobs) {
                Trigger trigger = executingJob.getTrigger();
                JobDetail jobDetail = executingJob.getJobDetail();
                JobKey jobKey = jobDetail.getKey();
                scheduleJobs.add(this.buildJobInfo(instance, jobKey, trigger));
            }
            return scheduleJobs;
        } catch (Exception e) {
            throw new ServiceException(ResultCode.ERROR_SEARCH, e);
        }
    }

    public boolean existScheduleJob(ScheduleExecution execution) throws ServiceException {
        try {
            return this.getScheduler().checkExists(JobKey.jobKey(QuartzUtils.getJobName(execution.getJobClass(), execution.getJobMethod(), execution.getJobArguments()), execution.getJobGroup()));
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public void pauseScheduleJob(ScheduleExecution execution) throws ServiceException {
        try {
            //pauseTrigger 和 resumeTrigger 会禁止下次执行  一个job对应多个trigger
            this.getScheduler().pauseJob(JobKey.jobKey(QuartzUtils.getJobName(execution.getJobClass(), execution.getJobMethod(), execution.getJobArguments()), execution.getJobGroup()));
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public void resumeScheduleJob(ScheduleExecution execution) throws ServiceException {
        try {
            this.getScheduler().resumeJob(JobKey.jobKey(QuartzUtils.getJobName(execution.getJobClass(), execution.getJobMethod(), execution.getJobArguments()), execution.getJobGroup()));
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }


    public void deleteScheduleJob(ScheduleExecution execution) throws ServiceException {
        try {
            this.getScheduler().deleteJob(JobKey.jobKey(QuartzUtils.getJobName(execution.getJobClass(), execution.getJobMethod(), execution.getJobArguments()), execution.getJobGroup()));
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public void addScheduleJob(ScheduleExecution execution) throws ServiceException {

        try {
            DefaultListableBeanFactory acf = (DefaultListableBeanFactory) getApplicationContext().getAutowireCapableBeanFactory();
            //注册 执行类
            String beanName= acf.getBeanNamesForType(Class.forName(execution.getJobClass()))[0];
            String jobName = QuartzUtils.getJobName(execution.getJobClass(), execution.getJobMethod(), execution.getJobArguments());
//            String beanName = QuartzUtils.getBeanName(execution.getJobClass());
            BeanDefinitionBuilder jobDetailBuilder = QuartzUtils.builderJobDetail(jobName, execution.getJobGroup(), beanName, execution.getJobMethod(), execution.getJobArguments(), execution.isConcurrent(), execution.isDurability(), execution.isRecovery());
            acf.registerBeanDefinition(jobName, jobDetailBuilder.getBeanDefinition());
            Object jobDetail = acf.getBean(jobName);
            //注册 调度器
            String jobTriggerName = QuartzUtils.getJobTriggerName(execution.getJobClass(), execution.getJobMethod(), execution.getJobArguments());
            BeanDefinitionBuilder triggerBuilder = QuartzUtils.builderJobTrigger(jobDetail, execution.getJobCronExpression(), execution.getJobGroup());
            acf.registerBeanDefinition(jobTriggerName, triggerBuilder.getBeanDefinition());

            Trigger trigger = (Trigger) acf.getBean(jobTriggerName);

            boolean triggerExists = this.getScheduler().checkExists(trigger.getKey());
            JobDetail job = (JobDetail) trigger.getJobDataMap().remove("jobDetail");
            if (triggerExists) {
                if (this.getScheduler().checkExists(job.getKey())) {
                    this.getScheduler().addJob(job, true);
                }
                Date rescheduleDate = this.getScheduler().rescheduleJob(trigger.getKey(), trigger);
                logger.info("recheduleJob Date {}", rescheduleDate);
                return;
            }
            try {
                this.getScheduler().scheduleJob(job, trigger);
            } catch (ObjectAlreadyExistsException alreadyException) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Unexpectedly found existing trigger, assumably due to cluster race condition: {} - can safely be ignored", alreadyException.getMessage());
                }
                this.getScheduler().rescheduleJob(trigger.getKey(), trigger);
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void updateScheduleJob(ScheduleExecution execution) throws ServiceException {
        try {
            String jobTriggerName = QuartzUtils.getJobTriggerName(execution.getJobClass(), execution.getJobMethod(), execution.getJobArguments());
            TriggerKey triggerKey = TriggerKey.triggerKey(jobTriggerName, execution.getJobGroup());
            CronTrigger trigger = (CronTrigger) this.getScheduler().getTrigger(triggerKey);
            //表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(execution.getJobCronExpression());
            //按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            //按新的trigger重新设置job执行
            this.getScheduler().rescheduleJob(triggerKey, trigger);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void triggerScheduleJob(ScheduleExecution execution) throws ServiceException {
        try {
            this.getScheduler().triggerJob(JobKey.jobKey(QuartzUtils.getJobName(execution.getJobClass(), execution.getJobMethod(), execution.getJobArguments()), execution.getJobGroup()));
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
}
