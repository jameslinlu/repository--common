/*
 * 文  件  名：MysqlDialect.java
 * 版         权：Copyright 2014 GSOFT Tech.Co.Ltd.All Rights Reserved.
 * 描         述：
 * 修  改  人：hadoop
 * 修改时间：2015年2月11日
 * 修改内容：新增
 */
package com.commons.store.orm.mybatis.dialect.impl;


import com.commons.metadata.model.PageParam;
import com.commons.store.orm.mybatis.constant.Const;
import com.commons.store.orm.mybatis.dialect.Dialect;

import java.util.HashMap;
import java.util.Map;

/**
 * mysql方言
 *
 * @author gj
 * @version 2015年2月11日
 */
public class MySqlDialect extends Dialect {

    /**
     * {@inheritDoc}
     */
    @Override
    public String generatePageSQL(String sql) {
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 14);
        sqlBuilder.append(sql);
        sqlBuilder.append(" limit ?,?");
        return sqlBuilder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateCountSQL(String sql) {
        return parser.getSmartCountSql(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getPageParameter(PageParam page) {
        Map<String, Object> param = new HashMap<String, Object>();
        if (page.getPageNo() <= 0) {
            page.setPageNo(1);
        }
        if (page.getPageSize() <= 0) {
            page.setPageSize(10);
        }
        param.put(Const.PARAMETER_FIRST, (page.getPageNo() - 1) * page.getPageSize());
        param.put(Const.PARAMETER_SECOND, page.getPageSize());
        return param;
    }

}
