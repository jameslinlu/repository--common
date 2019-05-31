package com.commons.common.utils.poi;

import com.commons.metadata.exception.ServiceException;

/**
 * Copyright (C)
 * ExcelHandler
 * Author: jameslinlu
 */
public interface ExcelHandler {
    void startRow(int rownum);

    void endRow(int rownum);

    void cell(String cell, String value);

    void open() throws ServiceException;

    void close() throws ServiceException;
}
