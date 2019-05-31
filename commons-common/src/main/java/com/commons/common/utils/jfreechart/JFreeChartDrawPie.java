package com.commons.common.utils.jfreechart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RectangleInsets;

import java.awt.*;
import java.util.Iterator;
import java.util.Map;

/**
 * Copyright (C)
 * JFreeChartDrawPie 绘制饼图
 * Author: jameslinlu
 */
public class JFreeChartDrawPie extends JFreeChartDraw<Map<String, ?>> {

    @Override
    public void setDataSet(Map<String, ?> oriData) {
        DefaultPieDataset dataSet = new DefaultPieDataset();
        Iterator<String> it = oriData.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            Double value;
            try {
                value = Double.valueOf(oriData.get(key).toString());
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Cast " + oriData.get(key).toString() + " to double get an exception");
            }
            dataSet.setValue(key, value);
        }
        this.dataSet = dataSet;
    }

    @Override
    public void drawJFreeChart() {
        chart = ChartFactory.createPieChart(params.getTitle(), (PieDataset) dataSet, true, true, false);
        LegendTitle legend = chart.getLegend(0);
        PiePlot plot = (PiePlot) chart.getPlot();

        chart.setTextAntiAlias(false);   //设置文本抗锯齿
        chart.setTitle(new TextTitle(params.getTitle()));

        legend.setItemFont(params.getLegendFont());
        plot.setLabelFont(params.getLabelFont());

        plot.setNoDataMessage(NO_DATA_MSG);
        plot.setForegroundAlpha(0.6f);
        plot.setInsets(new RectangleInsets(0, 0, 0, 0));
        plot.setCircular(true);// 圆形

        plot.setLabelGap(0.01);
        plot.setInteriorGap(0.05D);
        plot.setLegendItemShape(new Rectangle(10, 10)); // 图例形状
        plot.setIgnoreNullValues(true);
        plot.setLabelBackgroundPaint(null);  // 去掉背景色
        plot.setLabelShadowPaint(null);      // 去掉阴影
        plot.setLabelOutlinePaint(null);     // 去掉边框
        plot.setShadowPaint(null);

        // 0:category 1:value:2 :percentage
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}:{2}"));// 显示标签数据
    }
}
