package com.commons.metadata.model;

import java.util.Arrays;
import java.util.List;


public abstract class Model {

    public String sortField;// field
    public String sortType; //desc asc
    /**
     * 主键集合
     */
    protected transient List<String> primaries;

    /**
     * 获取实体类主键 用于便捷批量SQL
     */
    public abstract Object getPrimary();

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    /**
     * 输出排序语句
     *
     * @return
     */
    public String getOrderBy() {

        StringBuffer orders = new StringBuffer();
        String[] types = null;
        if (sortType != null) {
            types = sortType.split(",");
        }
        if (sortField != null) {
            String[] fields = sortField.split(",");
            for (int i = 0; i < fields.length; i++) {
                String field = fields[i].replaceAll(" ", "");
                if (field.trim().equals("")) {
                    continue;
                }
                int split = field.indexOf(".");
                field = (split == -1 ? "" : field.substring(0, split)) + revertCamel(field.substring(split == -1 ? 0 : split));
                field = escape(field);
                if (types != null && types.length - 1 >= i) {
                    //合法
                    field += types[i].trim().equalsIgnoreCase("desc") ? " desc" : " asc";
                } else {
                    //默认 desc
                    field += " desc";
                }
                if (i != fields.length - 1) {
                    field += ",";
                }
                orders.append(field.trim());
            }
        }
        if (!orders.toString().trim().equals("")) {
            return " order by " + orders.toString();
        }
        return "";
    }

    /**
     * 根据类型转换主键
     * 主键生成完为String类型 关联表依然是Integer或Long等等
     * 关联的实体 set主键时 不需要手动Integer.valueOf等等转换
     */
    public <T> T getPrimaryByClazz(Class<T> clazz) {
        Object obj = getPrimary();
        if (obj == null) {
            return null;
        }
        if (clazz == String.class) {
            return (T) String.valueOf(obj);
        }
        if (clazz == Integer.class) {
            return (T) Integer.valueOf(String.valueOf(obj));
        }
        return (T) obj;
    }

    public List<String> getPrimaries() {
        if (this.getPrimary() != null) {
            List<String> primarys = Arrays.asList(String.valueOf(this.getPrimary()).split(","));
            return primarys.isEmpty() ? null : primarys;
        }
        return null;
    }

    public static String revertCamel(String str) {
        return str.replaceAll("[A-Z]", "_$0").toLowerCase();
    }

    public static String escape(String str) {
        return str.replaceAll("'", "").replaceAll("<", "").replaceAll(">", "")
                .replaceAll("=", "").replaceAll("\\|", "").replaceAll(",", "")
                .replaceAll("-", "").replaceAll(";", "").replaceAll("&", "")
                .replaceAll("!", "").replaceAll("\\)", "").replaceAll("\\(", "");
    }

    public enum Determine {

        NO(0, "否"),
        YES(1, "是"),;
        /**
         * 编号
         */
        private Integer code;

        /**
         * 描述
         */
        private String desc;

        private Determine(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }


        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
}
