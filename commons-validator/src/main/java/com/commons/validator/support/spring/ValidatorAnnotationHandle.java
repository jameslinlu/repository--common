package com.commons.validator.support.spring;

import com.commons.metadata.model.validator.IValidator;
import com.commons.metadata.model.validator.annotation.Valid;
import com.commons.validator.cache.ValidatorCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 处理@Valid注解
 * 程序初始化后扫描Valid注解缓存至ValidatorCache
 */
public class ValidatorAnnotationHandle extends ApplicationObjectSupport implements BeanPostProcessor, InitializingBean {


    private static final Logger logger = LoggerFactory.getLogger(ValidatorAnnotationHandle.class);


    private void cacheValidator(Object bean) throws Exception {
        //缓存bean 和 method的注释 验证类
        //对方法上的校验可进行全局校验
        Class target = Class.forName(AopUtils.getTargetClass(bean).getName());
        Valid valid = AnnotationUtils.findAnnotation(target, Valid.class);
        if (valid == null) {
            return;
        }
        Class[] beanValidator = valid.value();
        List<IValidator> validators = null;
        for (Method method : target.getMethods()) {
            valid = AnnotationUtils.findAnnotation(method, Valid.class);
            if (valid == null) {
                continue;
            }
            validators = new LinkedList<>();
            validators.addAll(this.newValidator(valid.value()));
            validators.addAll(this.newValidator(beanValidator));
            ValidatorCache.setValidatorSign(method.toGenericString(), validators);
        }
    }


    private List<IValidator> newValidator(Class<? extends IValidator>[] classes) throws Exception {
        //初始化 注解指定的class
        List<IValidator> validators = new LinkedList<>();
        if (classes == null || classes.length == 0) {
            return validators;
        }
        for (Class clazz : classes) {
            IValidator validator = (IValidator) clazz.newInstance();
            validator.initialize();
            validators.add(validator);
        }
        return validators;
    }


    public void cacheUrlMappingValidator(ApplicationContext applicationContext) {
        RequestMappingHandlerMapping handlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        if (handlerMapping == null) {
            return;
        }
        Map map = handlerMapping.getHandlerMethods();
        Iterator<?> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            RequestMappingInfo mappingInfo = (RequestMappingInfo) entry.getKey();
            String url = mappingInfo.getPatternsCondition().getPatterns().iterator().next();
            HandlerMethod handlerMethod = ((HandlerMethod) entry.getValue());
            Valid valid = handlerMethod.getMethodAnnotation(Valid.class);
            if (valid == null) {
                continue;
            }
            ValidatorCache.setSign(url, handlerMethod.getMethod().toGenericString());
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.cacheUrlMappingValidator(getApplicationContext());
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        try {
            this.cacheValidator(bean);
        } catch (Exception e) {
            logger.error("Cache Validator Fail With {}", beanName, e);
        }
        return bean;
    }
}
