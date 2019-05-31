package com.commons.common.utils.jfreechart;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PieLabelLinkStyle;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.data.general.Dataset;
import org.jfree.ui.RectangleInsets;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Copyright (C)
 * JFreeChartDraw 绘制图像模板类
 * Author: jameslinlu
 */
public abstract class JFreeChartDraw<T> {

    protected final static String NO_DATA_MSG = "数据加载失败";
    public static Color[] CHART_COLORS = {
            new Color(31, 129, 188), new Color(92, 92, 97), new Color(144, 237, 125), new Color(255, 188, 117),
            new Color(153, 158, 255), new Color(255, 117, 153), new Color(253, 236, 109), new Color(128, 133, 232),
            new Color(158, 90, 102), new Color(255, 204, 102)};
    private static Font FONT = new Font("宋体", Font.PLAIN, 16);
    protected Dataset dataSet;
    protected JFreeChart chart;
    protected JFreeChartParameters params;

    protected abstract void setDataSet(T oriData);

    protected abstract void drawJFreeChart();

    public void process(JFreeChartParameters params, T oriData, OutputStream out) {

        setParams(params);

        setChartTheme();
        setDataSet(oriData);
        drawJFreeChart();

        writeChartAsImg(out);
    }

    public BufferedImage process(JFreeChartParameters params, T oriData) {
        setParams(params);
        setChartTheme();
        setDataSet(oriData);
        drawJFreeChart();
        return getChartAsImg();
    }

    public void setParams(JFreeChartParameters params) {
        this.params = params;
    }

    /**
     * 设置表格的字体样式
     */
    private void setChartTheme() {
        StandardChartTheme chartTheme = new StandardChartTheme("CN");

        chartTheme.setExtraLargeFont(FONT);  // 设置标题字体
        chartTheme.setRegularFont(FONT);    // 设置图例的字体
        chartTheme.setLargeFont(FONT);      // 设置轴向的字体
        chartTheme.setSmallFont(FONT);

        chartTheme.setTitlePaint(new Color(51, 51, 51));     //标题字体颜色
        chartTheme.setSubtitlePaint(new Color(85, 85, 85));  //副标题字体颜色

        chartTheme.setChartBackgroundPaint(Color.WHITE);  //图表背景色
        chartTheme.setLegendBackgroundPaint(Color.WHITE);  // 设置标注背景色
        chartTheme.setLegendItemPaint(Color.BLACK);  //设置标注字体颜色

        // 绘制器颜色源
        Paint[] OUTLINE_PAINT_SEQUENCE = new Paint[]{Color.WHITE};  //外边框线条颜色
        DefaultDrawingSupplier drawingSupplier = new DefaultDrawingSupplier(CHART_COLORS, CHART_COLORS, OUTLINE_PAINT_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE);
        chartTheme.setDrawingSupplier(drawingSupplier);

        chartTheme.setPlotBackgroundPaint(Color.WHITE);// 绘制区域背景色
        chartTheme.setPlotOutlinePaint(Color.WHITE);// 绘制区域外边框
        chartTheme.setLabelLinkPaint(new Color(8, 55, 114));// 链接标签颜色
        chartTheme.setLabelLinkStyle(PieLabelLinkStyle.CUBIC_CURVE);

        chartTheme.setAxisOffset(new RectangleInsets(5, 12, 5, 12));  //设置X-Y坐标轴偏移量
        chartTheme.setDomainGridlinePaint(new Color(192, 208, 224));  // X坐标轴垂直网格颜色
        chartTheme.setRangeGridlinePaint(new Color(192, 192, 192));   // Y坐标轴水平网格颜色

        chartTheme.setBaselinePaint(Color.WHITE);
        chartTheme.setCrosshairPaint(Color.BLUE);
        chartTheme.setAxisLabelPaint(new Color(51, 51, 51));  // 坐标轴标题文字颜色
        chartTheme.setTickLabelPaint(new Color(67, 67, 72));  // 刻度数字
        chartTheme.setBarPainter(new StandardBarPainter());     // 设置柱状图渲染
        chartTheme.setXYBarPainter(new StandardXYBarPainter());  // XYBar 渲染

        chartTheme.setItemLabelPaint(Color.black);
        chartTheme.setThermometerPaint(Color.white);  //温度计

        ChartFactory.setChartTheme(chartTheme);
    }

    /**
     * 存储表格为 图片
     *
     * @param out
     */
    private void writeChartAsImg(OutputStream out) {
        try {
            ChartUtilities.writeChartAsPNG(out, chart, params.getChartWidth(), params.getChartHeight());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 存储表格为流
     */
    private BufferedImage getChartAsImg() {
        BufferedImage bufferedImage = chart.createBufferedImage(params.getChartWidth(), params.getChartHeight());
        return bufferedImage;
    }
}
