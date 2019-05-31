package com.commons.proxy.server.exporter.impl;

import com.commons.common.utils.PropUtil;
import com.commons.proxy.center.model.ServiceInfo;
import com.commons.proxy.center.registy.IProxyRegistry;
import com.commons.proxy.server.annotation.RemoteService;
import com.commons.proxy.server.exporter.IServiceExporter;
import com.commons.proxy.server.exporter.factory.ExporterFactory;
import com.commons.proxy.server.handler.HttpServiceExporter;
import com.commons.proxy.server.handler.MQServiceExporter;
import com.commons.proxy.server.handler.SecurityHessianServiceExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class RemoteServiceExporter extends ApplicationObjectSupport implements IServiceExporter {
    private static Logger logger = LoggerFactory.getLogger(RemoteServiceExporter.class);

    private IProxyRegistry proxyRegistry;
    private List<ServiceInfo> services = null;
    private String mqManagerKey;

    @Override
    public Class<? extends Annotation> supportAnnotationClass() {
        return RemoteService.class;
    }

    public void setProxyRegistry(IProxyRegistry proxyRegistry) {
        this.proxyRegistry = proxyRegistry;
    }

    public void setMqManagerKey(String mqManagerKey) {
        if (mqManagerKey.startsWith("$")) {
            this.mqManagerKey = PropUtil.getPlaceholder(mqManagerKey, null, null);
        } else {
            this.mqManagerKey = mqManagerKey;
        }
    }

    /**
     * 按类型发布
     */
    @Override
    public void exporter(Collection objects) throws Exception {
        services = new ArrayList<>(objects.size());
        ServiceInfo service = null;
        for (Object bean : objects) {
            //获取类注解
            RemoteService remoteService = AnnotationUtils.findAnnotation(Class.forName(AopUtils.getTargetClass(bean).getName()), RemoteService.class);
            //获取注解参数
            String name = remoteService.name().equals("") ? AopUtils.getTargetClass(bean).getSimpleName() : remoteService.name();
            String version = String.valueOf(remoteService.version());
            String interfaceName = remoteService.serviceInterface().getName();
            String serializer = remoteService.serialize().toString();
            String transfer = remoteService.transfer().toString();
            float timeout = remoteService.timeout();
            float delay = remoteService.delay();
            boolean enable = remoteService.enable();
            //发布
            this.doExporter(name, bean, remoteService);
            //记录发布服务
            service = new ServiceInfo(name, version, serializer, transfer, interfaceName, enable, timeout,delay);
            services.add(service);
        }

        if (proxyRegistry != null && !services.isEmpty()) {
            proxyRegistry.register(services);
        }
    }

    @Override
    public void doExporter(String beanName, Object bean, Object annotation) throws Exception {
        RemoteService service = (RemoteService) annotation;
        DefaultListableBeanFactory acf = (DefaultListableBeanFactory) getApplicationContext().getAutowireCapableBeanFactory();
        Class clazz = ExporterFactory.buildClazz(service.transfer());

        BeanDefinitionBuilder mb = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        mb.addPropertyValue("service", bean);
        mb.addPropertyValue("serviceInterface", service.serviceInterface());
        if (proxyRegistry != null) {
            mb.addPropertyValue("config", proxyRegistry.getProxyConfig());
        }
        if (clazz.isAssignableFrom(HttpServiceExporter.class) || clazz.isAssignableFrom(SecurityHessianServiceExporter.class)) {
            if (!beanName.startsWith("/")) {
                beanName = "/" + beanName;
            }
        }
        if (clazz.isAssignableFrom(HttpServiceExporter.class)) {
            mb.addPropertyValue("mqManagerKey", this.mqManagerKey);
        }
        if (clazz.isAssignableFrom(MQServiceExporter.class)) {
            mb.addPropertyValue("delay", service.delay());
            mb.addPropertyValue("timeout", service.timeout());
            mb.addPropertyValue("mqManagerKey", this.mqManagerKey);
        }
        acf.registerBeanDefinition(beanName, mb.getBeanDefinition());
    }

}
