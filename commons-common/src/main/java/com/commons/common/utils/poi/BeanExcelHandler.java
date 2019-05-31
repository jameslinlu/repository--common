package com.commons.common.utils.poi;

import com.commons.common.utils.Reflections;
import com.commons.metadata.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
    String[] fields = "customerId,customerTrade,customerName,zonename,serviceName,customerTypeDesc,person1,mobile1,tel1,email1,person2,mobile2,tel2,email2,blackholeLimit,cleanLimit,blockIp,cleanIp,equips".split(",");
    POIUtils.parseExcel2007(new ByteArrayInputStream(customExcel), new BeanExcelHandler<>(MigrateCustom.class, fields, new BeanExcelHandler.BeanHandler<MigrateCustom>() {
        @Override
        public void row(MigrateCustom bean) {
            migrateCustoms.add(bean);
        }
    }));
*/
public class BeanExcelHandler<T> implements ExcelHandler {

    private static final Logger logger = LoggerFactory.getLogger(BeanExcelHandler.class);

    public interface BeanHandler<T> {
        //每行数据构成的bean
        void row(T bean);
    }

    //数据字段 按excel的顺序 为空则跳过
    //date#转换为日期
    private String[] fields;
    private Class clazz;
    private Object target;
    //每行数据处理类
    private BeanHandler<T> handler;
    private int rowIndex = 0;
    private int effectRowIndex = 1;//从第几行开始有效数据 默认为1
    private boolean useEffectRow = true;//使用 有效行过滤

    public void setEffectRowIndex(int effectRowIndex) {
        this.effectRowIndex = effectRowIndex;
    }

    public void setUseEffectRow(boolean useEffectRow) {
        this.useEffectRow = useEffectRow;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public void setHandler(BeanHandler<T> handler) {
        this.handler = handler;
    }

    public BeanExcelHandler(Class clazz, String[] fields, BeanHandler<T> handler) {
        this.fields = fields;
        this.clazz = clazz;
        this.handler = handler;
    }

    public Object newTarget() throws Exception {
        return clazz.newInstance();
    }

    @Override
    public void startRow(int rownum) {
        logger.info("startRow");
        try {
            target = newTarget();
            rowIndex = rownum;
        } catch (Exception e) {
            logger.error("Excel new Target Bean Fail ", e);
        }
    }

    @Override
    public void endRow(int rownum) {
        logger.info("endRow");
        if (rowIndex < effectRowIndex && useEffectRow) {
            logger.info(" skip row {} ", rownum);
            return;
        }
        if (target != null) {
            handler.row((T) target);
        }
    }

    @Override
    public void cell(String cell, String value) {
        if (rowIndex < effectRowIndex && useEffectRow) {
            logger.info(" skip cell {} {}", cell, value);
            return;
        }
        logger.info("cell {} {}", cell, value);
        int cellIndex = 0;
        for (int i = 0; i < cell.length(); i++) {
            int code = Character.codePointAt(cell, i);
            if (code >= 65 && code <= 90) {
                code -= 65;
                cellIndex += code;
            }
        }

        if (cellIndex < fields.length) {
            String fieldName = fields[cellIndex];
            try {
                Reflections.setFieldValue(target, fieldName, value);
            } catch (Exception e) {
                logger.error(" set field value fail {} = {}", fieldName, value);
            }
        }
    }

    @Override
    public void open() throws ServiceException {
        logger.info("open");
    }

    @Override
    public void close() throws ServiceException {
        logger.info("close");
    }
}