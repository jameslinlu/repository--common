package com.commons.store.orm.mybatis.dialect;

import com.commons.metadata.model.PageParam;
import com.commons.store.orm.mybatis.utils.SqlParser;

import java.util.Map;

/**
 * 数据库方言抽象类
 **/
public abstract class Dialect {

    public SqlParser parser = new SqlParser();

    /**
     * 生成分页查询sql语句
     *
     * @param sql 原始列表sql
     * @return
     * @author gj
     */
    public abstract String generatePageSQL(String sql);

    /**
     * 生成 总数查询sql语句
     *
     * @param sql 原始列表sql
     * @return
     * @author gj
     */
    public abstract String generateCountSQL(String sql);

    /**
     * 设置分页参数 Const中 First 和 Second  配合PageSql中的占位符  put值到param中
     *
     * @param page
     * @return
     * @author hadoop
     */
    public abstract Map<String, Object> getPageParameter(PageParam page);


}
