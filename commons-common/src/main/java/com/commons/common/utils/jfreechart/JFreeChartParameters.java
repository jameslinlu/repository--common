package com.commons.common.utils.jfreechart;

import org.apache.commons.lang3.StringUtils;

import java.awt.*;

/**
 * Copyright (C)
 * JFreeChartParameters  JFreeChart配置参数
 * Author: jameslinlu
 */
public class JFreeChartParameters {

    private final static int DEFAULT_CHART_WIDTH = 400;
    private final static int DEFAULT_CHART_HEIGHT = 300;

    private final static String DEFAULT_TITLE = "";
    private final static Font DEFAULT_TITLE_FONT = new Font("微软雅黑", Font.BOLD, 16);
    private final static Font DEFAULT_LEGEND_FONT = new Font("微软雅黑", Font.BOLD, 14);
    private final static Font DEFAULT_LABEL_FONT = new Font("SansSerif", Font.ITALIC, 14);

    private Integer chartWidth;
    private Integer chartHeight;
    private String title;
    private String xTitle;
    private String yTitle;

    private Font titleFont;
    private Font legendFont;
    private Font labelFont;

    public Integer getChartWidth() {
        if (null == this.chartWidth)
            return DEFAULT_CHART_WIDTH;
        return chartWidth;
    }

    public void setChartWidth(Integer chartWidth) {
        this.chartWidth = chartWidth;
    }

    public Integer getChartHeight() {
        if (null == this.chartHeight)
            return DEFAULT_CHART_HEIGHT;
        return chartHeight;
    }

    public void setChartHeight(Integer chartHeight) {
        this.chartHeight = chartHeight;
    }

    public String getTitle() {
        if (StringUtils.isEmpty(this.title))
            return DEFAULT_TITLE;
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getxTitle() {
        if (StringUtils.isEmpty(this.xTitle))
            return DEFAULT_TITLE;
        return xTitle;
    }

    public void setxTitle(String xTitle) {
        this.xTitle = xTitle;
    }

    public String getyTitle() {
        if (StringUtils.isEmpty(this.yTitle))
            return DEFAULT_TITLE;
        return yTitle;
    }

    public void setyTitle(String yTitle) {
        this.yTitle = yTitle;
    }

    public Font getTitleFont() {
        if (null == this.titleFont)
            return DEFAULT_TITLE_FONT;
        return titleFont;
    }

    public void setTitleFont(Font titleFont) {
        this.titleFont = titleFont;
    }

    public Font getLegendFont() {
        if (null == this.legendFont)
            return DEFAULT_LEGEND_FONT;
        return legendFont;
    }

    public void setLegendFont(Font legendFont) {
        this.legendFont = legendFont;
    }

    public Font getLabelFont() {
        if (null == this.labelFont)
            return DEFAULT_LABEL_FONT;
        return labelFont;
    }

    public void setLabelFont(Font labelFont) {
        this.labelFont = labelFont;
    }
}
