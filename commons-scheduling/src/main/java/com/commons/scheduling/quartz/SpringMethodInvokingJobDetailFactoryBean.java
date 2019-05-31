package com.commons.scheduling.quartz;

import com.commons.log.utils.LoggerContextUtil;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.*;
import org.springframework.util.ClassUtils;
import org.springframework.util.MethodInvoker;


public class SpringMethodInvokingJobDetailFactoryBean
        implements FactoryBean<JobDetail>, BeanNameAware, BeanClassLoaderAware, BeanFactoryAware, InitializingBean {

    private String name;

    private String group = Scheduler.DEFAULT_GROUP;

    private boolean concurrent = true;
    private boolean durability = true;
    private boolean requestsRecovery = true;


    private String targetBeanName;
    private String targetMethod;
    private Object[] targetArguments = new Object[0];
    private String beanName;

    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    private static BeanFactory beanFactory;

    private JobDetail jobDetail;

    public void setTargetMethod(String targetMethod) {
        this.targetMethod = targetMethod;
    }

    public void setRequestsRecovery(boolean requestsRecovery) {
        this.requestsRecovery = requestsRecovery;
    }

    public void setDurability(boolean durability) {
        this.durability = durability;
    }

    /**
     * Set the name of the job.
     * <p>Default is the bean name of this FactoryBean.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the group of the job.
     * <p>Default is the default group of the Scheduler.
     *
     * @see org.quartz.Scheduler#DEFAULT_GROUP
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Specify whether or not multiple jobs should be run in a concurrent fashion.
     * The behavior when one does not want concurrent jobs to be executed is
     * realized through adding the {@code @PersistJobDataAfterExecution} and
     * {@code @DisallowConcurrentExecution} markers.
     * More information on stateful versus stateless jobs can be found
     * <a href="http://www.quartz-scheduler.org/documentation/quartz-2.1.x/tutorials/tutorial-lesson-03">here</a>.
     * <p>The default setting is to run jobs concurrently.
     */
    public void setConcurrent(boolean concurrent) {
        this.concurrent = concurrent;
    }

    public void setTargetBeanName(String targetBeanName) {
        this.targetBeanName = targetBeanName;
    }

    public void setTargetArguments(Object[] targetArguments) {
        this.targetArguments = targetArguments;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }


    @Override
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() throws ClassNotFoundException, NoSuchMethodException {

        // Use specific name if given, else fall back to bean name.
        String name = (this.name != null ? this.name : this.beanName);

        // Consider the concurrent flag to choose between stateful and stateless job.
        Class<?> jobClass = (this.concurrent ? MethodInvokingJob.class : StatefulMethodInvokingJob.class);

        // Build JobDetail instance.
        JobDetailImpl jdi = new JobDetailImpl();
        jdi.setName(name);
        jdi.setGroup(this.group);
        jdi.setJobClass((Class) jobClass);
        jdi.setDurability(this.durability);
        jdi.setRequestsRecovery(this.requestsRecovery);
        jdi.getJobDataMap().put("targetBeanName", targetBeanName);
        jdi.getJobDataMap().put("targetMethod", targetMethod);
        jdi.getJobDataMap().put("targetArguments", targetArguments);
        this.jobDetail = jdi;

    }

    @Override
    public JobDetail getObject() {
        return this.jobDetail;
    }

    @Override
    public Class<? extends JobDetail> getObjectType() {
        return (this.jobDetail != null ? this.jobDetail.getClass() : JobDetail.class);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }


    /**
     * Quartz Job implementation that invokes a specified method.
     * Automatically applied by MethodInvokingJobDetailFactoryBean.
     */
    public static class MethodInvokingJob implements Job {

        protected static final Logger logger = LoggerFactory.getLogger(MethodInvokingJob.class);

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            try {
                String targetBeanName = jobExecutionContext.getMergedJobDataMap().getString("targetBeanName");
                String targetMethod = jobExecutionContext.getMergedJobDataMap().getString("targetMethod");
                Object targetBean = beanFactory.getBean(targetBeanName);
                Object[] targetArguments = (Object[]) jobExecutionContext.getMergedJobDataMap().get("targetArguments");
                if (targetBeanName == null || targetBean == null)
                    throw new JobExecutionException("targetBean cannot be null.", false);
                if (targetMethod == null)
                    throw new JobExecutionException("targetMethod cannot be null.", false);

                LoggerContextUtil.reset();
                MethodInvoker beanMethod = new MethodInvoker();
                beanMethod.setTargetObject(targetBean);
                beanMethod.setTargetMethod(targetMethod);
                beanMethod.setArguments(targetArguments);
                beanMethod.prepare();
                logger.info("ScheduleJob Invoking Bean: " + targetBean + "; Method: " + targetMethod + ".");
                jobExecutionContext.setResult(beanMethod.invoke());
            } catch (JobExecutionException e) {
                throw e;
            } catch (Exception e) {
                throw new JobExecutionException(e);
            }
        }
    }


    /**
     * Extension of the MethodInvokingJob, implementing the StatefulJob interface.
     * Quartz checks whether or not jobs are stateful and if so,
     * won't let jobs interfere with each other.
     */
    @PersistJobDataAfterExecution
    @DisallowConcurrentExecution
    public static class StatefulMethodInvokingJob extends MethodInvokingJob {

        // No implementation, just an addition of the tag interface StatefulJob
        // in order to allow stateful method invoking jobs.
    }

}