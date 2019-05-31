package com.commons.common.support.spring.security.filter;

import com.commons.metadata.exception.ServiceException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


/**
 * <p>Title: 系统安全过滤器/p>
 * <p>Description:系统安全过滤器，对一些请求做一些过滤</p>
 * <p>Copyright: Copyright (c) 2016 zhong-ying.com Inc.
 * All right reserved.</p>
 *
 * @author: 顾庆崴
 */
public interface SecurityFilter {
    //判断请求是否满足处理条件，满足条件才会进行过滤
    Boolean accept(String acceptFilterTypeName);

    //对请求进行锅炉
    Boolean filter(HttpServletRequest request, HttpServletResponse response, Map<String, String> attributes) throws Exception;

    //filer的类型
    String getType();
}
