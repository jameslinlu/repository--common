package com.commons.scheduling.annotation;


import org.quartz.Scheduler;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Job {
    //cron表达式 配置于文件中或直接写
    String cron() default "";

    //分组名称：用户Service的定时 或其他Service的定时部署在不同容器中
    String group() default Scheduler.DEFAULT_GROUP;

    //是否并发执行  同一个job按顺序一个个执行
    boolean concurrent() default true;

    //是否持久化  若false并且无trigger关联则被删除
    boolean durability() default true;

    //中断恢复  若非正常退出,持久化任务会在程序启动后 执行错过的任务
    boolean recovery() default true;
}
