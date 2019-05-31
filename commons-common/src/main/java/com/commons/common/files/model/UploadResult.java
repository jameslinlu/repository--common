package com.commons.common.files.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C)
 * UploadResult
 * Author: jameslinlu
 */
public class UploadResult implements Serializable {
    private Map<String, List<UploadFile>> files;

    public Map<String, List<UploadFile>> getFiles() {
        return files;
    }

    public void setFiles(Map<String, List<UploadFile>> files) {
        this.files = files;
    }

}
