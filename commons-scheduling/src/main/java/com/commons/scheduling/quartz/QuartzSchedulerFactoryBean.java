package com.commons.scheduling.quartz;

import com.commons.common.utils.PropUtil;
import com.commons.scheduling.annotation.Job;
import com.commons.scheduling.util.QuartzUtils;
import org.quartz.Trigger;
import org.quartz.impl.SchedulerRepository;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class QuartzSchedulerFactoryBean extends SchedulerFactoryBean {
    private static final Logger logger = LoggerFactory.getLogger(QuartzSchedulerFactoryBean.class);
    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap(64));
    private ApplicationContext applicationContext;
    private DefaultListableBeanFactory acf;
    List<Trigger> triggersList;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setConfigLocation(Resource configLocation) {
        super.setConfigLocation(configLocation);
        coverName(configLocation);
    }

    /**
     * 设置BeanName ,beanName覆盖 schedulerName，schedulerName为org.quartz.scheduler.instanceName
     * 运行时通过SCHED_NAME(instanceName)过滤需要执行的Job
     *
     * @param configLocation quartz.properties
     */
    private void coverName(Resource configLocation) {
        String name = null;
        try {
            Properties mergedProps = new Properties();
            PropertiesLoaderUtils.fillProperties(mergedProps, configLocation);
            name = mergedProps.getProperty(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME);
            setBeanName(name);
        } catch (IOException e) {
            logger.error("Covert Schedule Instance Name to {} Fail", name);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            acf = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
            Field field = ReflectionUtils.findField(SchedulerFactoryBean.class, "triggers");
            ReflectionUtils.makeAccessible(field);
            Object object = ReflectionUtils.getField(field, this);
            triggersList = (List<Trigger>) object;
            if (triggersList == null) {
                triggersList = new ArrayList<>();
            }
            for (String beanName : this.applicationContext.getBeanDefinitionNames()) {
                if (beanName.startsWith(QuartzSchedulerFactoryBean.class.getName())) {
                    continue;
                }
                final Object bean = this.applicationContext.getBean(beanName);
                Class targetClass = AopUtils.getTargetClass(bean);
                if (!this.nonAnnotatedClasses.contains(targetClass)) {
                    final LinkedHashSet annotatedMethods = new LinkedHashSet(1);
                    ReflectionUtils.doWithMethods(targetClass, new ReflectionUtils.MethodCallback() {
                        public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                            Job jobAnnotation = AnnotationUtils.getAnnotation(method, Job.class);
                            if (jobAnnotation != null) {
                                try {
                                    processJob(jobAnnotation,beanName, method, bean);
                                    annotatedMethods.add(method);
                                } catch (Exception e) {
                                    logger.error("Process Job Annotation Fail {} {}", bean, method);
                                }
                            }
                        }
                    });
                    if (annotatedMethods.isEmpty()) {
                        this.nonAnnotatedClasses.add(targetClass);
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("No @Job annotations found on bean class: " + bean.getClass());
                        }
                    } else if (this.logger.isDebugEnabled()) {
                        this.logger.debug(annotatedMethods.size() + " @Job methods processed on bean \'" + bean.getClass() + "\': " + annotatedMethods);
                    }
                }
            }
            if (!triggersList.isEmpty()) {
                ReflectionUtils.setField(field, this, triggersList);
            }
        } catch (Exception e) {
            logger.error("Scanner Job Annotation Throw Exception", e);
            throw e;
        }
        super.afterPropertiesSet();

        //后期需动态添加定时任务时使用此实例扩展工具
//        SchedulerRepository repository = SchedulerRepository.getInstance();
    }

    private void processJob(Job jobAnnotation,String beanName, Method method, Object bean) throws Exception {

        Class jobClass = AopUtils.getTargetClass(bean);
        String jobMethod = method.getName();
        Object[] jobArguments = new Object[0];
        String jobGroup = jobAnnotation.group();
        Pattern p = Pattern.compile("\\$\\{(.+)\\}");
        Matcher m = p.matcher(jobAnnotation.cron());
        String cronExpression = "";
        if (m.find()) {
            cronExpression = PropUtil.get(m.group(1).trim());
        } else {
            cronExpression = jobAnnotation.cron();
        }
        if (StringUtils.isEmpty(cronExpression)) {
            logger.error("{} {} cron not found ! please check !", jobClass, jobMethod);
            return;
        }
        //注册 执行类
        String jobName = QuartzUtils.getJobName(jobClass.getName(), jobMethod, jobArguments);
//        String beanName = QuartzUtils.getBeanName(jobClass.getName());
        boolean concurrent = jobAnnotation.concurrent();
        boolean durability = jobAnnotation.durability();
        boolean recovery = jobAnnotation.recovery();
        BeanDefinitionBuilder jobDetailBuilder = QuartzUtils.builderJobDetail(jobName, jobGroup, beanName, jobMethod, jobArguments, concurrent, durability, recovery);
        acf.registerBeanDefinition(jobName, jobDetailBuilder.getBeanDefinition());
        Object jobDetail = acf.getBean(jobName);
        //注册 调度器
        String jobTriggerName = QuartzUtils.getJobTriggerName(jobClass.getName(), jobMethod, jobArguments);
        BeanDefinitionBuilder triggerBuilder = QuartzUtils.builderJobTrigger(jobDetail, cronExpression, jobGroup);
        acf.registerBeanDefinition(jobTriggerName, triggerBuilder.getBeanDefinition());

        this.triggersList.add((Trigger) acf.getBean(jobTriggerName));
    }

}
