package com.commons.common.files.model;

import java.io.Serializable;

public class UploadFile implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8076656833698703299L;
    private String rawname;// 文件原名称 含后缀
    private String suffix;// 文件后缀
    private String size;// 文件大小 M
    private String filepath;// 相对路径 含文件
    private String name;// 文件转码后名称 含后缀
    private String fullPath;// 全路径

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }


    public String getRawname() {
        return rawname;
    }

    public void setRawname(String rawname) {
        this.rawname = rawname;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UploadFile [rawname=" + rawname + ", suffix="
                + suffix + ", size=" + size + ", filepath=" + filepath
                + ", name=" + name + "]";
    }


}
