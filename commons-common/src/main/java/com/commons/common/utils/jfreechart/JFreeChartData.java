package com.commons.common.utils.jfreechart;

/**
 * Copyright (C)
 * JFreeChartData 柱状图元数据
 * Author: jameslinlu
 */
public class JFreeChartData {

    private Number number;
    private String row;
    private String col;

    public JFreeChartData(Number number, String row, String col) {
        this.number = number;
        this.row = row;
        this.col = col;
    }

    public Number getNumber() {
        return number;
    }

    public void setNumber(Number number) {
        this.number = number;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getCol() {
        return col;
    }

    public void setCol(String col) {
        this.col = col;
    }
}
