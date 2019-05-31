package com.commons.common.utils.jfreechart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleInsets;

import java.util.List;

/**
 * Copyright (C)
 * JFreeChartDrawBar 绘制柱状图
 * Author: jameslinlu
 */
public class JFreeChartDrawBar extends JFreeChartDraw<List<JFreeChartData>> {

    @Override
    public void setDataSet(List<JFreeChartData> oriData) {
        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
        for (int i = 0; i < oriData.size(); i++) {
            JFreeChartData elem = oriData.get(i);
            dataSet.addValue(elem.getNumber(), elem.getRow(), elem.getCol());
        }
        this.dataSet = dataSet;
    }

    @Override
    public void drawJFreeChart() {
        chart = ChartFactory.createBarChart(params.getTitle(), params.getxTitle(), params.getyTitle(),
                (CategoryDataset) this.dataSet, PlotOrientation.VERTICAL, true, true, false);

        chart.setTitle(new TextTitle(params.getTitle()));
        LegendTitle legend = chart.getLegend(0);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis categoryAxis = plot.getDomainAxis();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        legend.setItemFont(params.getLegendFont());

        plot.getDomainAxis().setLabelFont(params.getLabelFont());
        plot.getRangeAxis().setLabelFont(params.getLabelFont());
        plot.setNoDataMessage(NO_DATA_MSG);
        plot.setInsets(new RectangleInsets(10, 10, 5, 10));

        categoryAxis.setTickLabelFont(params.getLabelFont());
        categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);// 倾斜45度

        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setMaximumBarWidth(0.075);// 设置柱子最大宽度
    }
}
