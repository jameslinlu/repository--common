package com.commons.configuration.aspectj;

import com.commons.configuration.ICommonConfiguration;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 集群系统参数 配置切面提取AOP
 */
public class CommonConfigurationAspectj {

    private ICommonConfiguration commonConfig;

    /**
     * 配置项的处理切面
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        try {
            Object returnValue = null;
            returnValue = pjp.proceed();
            commonConfig.extract();
            return returnValue;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    public ICommonConfiguration getCommonConfig() {
        return commonConfig;
    }

    public void setCommonConfig(ICommonConfiguration commonConfig) {
        this.commonConfig = commonConfig;
    }
}
