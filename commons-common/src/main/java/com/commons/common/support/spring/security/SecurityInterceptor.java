package com.commons.common.support.spring.security;

import com.commons.common.support.spring.security.filter.SecurityFilter;
import com.commons.common.utils.PropUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: 系统安全拦截器</p>
 * <p>Description: 系统安全拦截器，可用于做一些鉴权、权限限制之类的功能</p>
 * <p>Copyright: Copyright (c) 2016 zhong-ying.com Inc.
 * All right reserved.</p>
 *
 * @author: 顾庆崴
 */
public class SecurityInterceptor extends HandlerInterceptorAdapter implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(SecurityInterceptor.class);

    private String acceptFilters;//接受需要处理的Filters名称  由Filter的type返回匹配
    private String domain;//获取配置的domain
    private List<SecurityFilter> filters = new ArrayList<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //自动获取所有SecurityFilter实现
        Map<String, SecurityFilter> filterMap = applicationContext.getBeansOfType(SecurityFilter.class);
        for (String beanName : filterMap.keySet()) {
            filters.add(filterMap.get(beanName));
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Assert.notNull(acceptFilters);
        //将需要处理的Filter按逗号分隔
        List<String> types = Arrays.asList(acceptFilters.split(","));
        //遍历所有SecurityFilter实现
        for (SecurityFilter filter : filters) {
            //循环匹配filter的accept方法是否处理已开启的filter
            for (String type : types) {
                if (filter.accept(type)) {
                    //将此filter的扩展系统配置信息读取传递
                    Map<String, String> attributes = PropUtil.getPrefix(type, type + '.', "", this.getDomain());
                    if (!filter.filter(request, response, attributes)) {
                        return false;
                    }
                }
            }

        }
        return super.preHandle(request, response, handler);
    }


    public String getAcceptFilters() {
        return acceptFilters;
    }

    public void setAcceptFilters(String acceptFilters) {
        this.acceptFilters = acceptFilters;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
