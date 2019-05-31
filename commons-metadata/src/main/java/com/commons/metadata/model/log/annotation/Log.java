package com.commons.metadata.model.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Copyright @ 2015 yuzhouwan.com
 * All right reserved.
 * Function: com.commons.metadata.model.log.annotation
 *
 * @author jameslinlu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Log {

    // 日志可读性描述
    String description() default "";

    /**
     * 增强日志描述
     * <p>
     * #example
     * <code>@Log(description = "检测磁盘空间任务 userName: %s, xxx %s, %s%s%s", extract = "{'args':['1:userName', '2'], 'rets':['0', '2', '3:password']}")<code/>
     * <p>
     * <p>
     * #规则
     * args:[i, ...]				表示从 参数列表中获取第 i个值
     * rets:[i, ...] & i = 0		表示获取 整个返回值，例如 integer/string... 基础类型
     * rets:[i, ...] & i > 0		表示从 返回的 array/list 等 collection 中，获取第 i 个元素
     * <p>
     * args:["j:xxx", ...]			表示从 参数列表中获取第 j个实体类的 xxx属性值
     * rets:["j:yyy", ...]			表示从 返回的 array/list 等 collection 中，获取第 i 个元素 的 yyy属性值
     * <p>
     * args:[]						表示从 参数列表中获取第 1个值
     * rets:[]						表示从 返回的 array/list 等 collection 中，获取第 1 个元素
     * <p>
     * #最后按照 顺序，替换入 description
     * #限制
     * a) 不支持 method(List<User> list) / method(Map<String, List<User>> map)此类复杂的 传入参数
     * b) 不支持 return List<List<User>> lists 此类大于 两层嵌套的返回值
     * c) 顺序要求，如果存在 args，需要写在 rets的前面
     * d) 所有的 index, 均需 >=0
     */
    String extract() default "";
}
