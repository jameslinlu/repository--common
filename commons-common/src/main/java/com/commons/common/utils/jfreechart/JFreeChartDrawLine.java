package com.commons.common.utils.jfreechart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.util.List;

/**
 * <p>Title: 折线图实现</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2016 zhong-ying.com Inc.
 * All right reserved.</p>
 *
 * @author: 许志成 on 2016/6/8.
 */
public class JFreeChartDrawLine extends JFreeChartDraw<List<JFreeChartData>> {

    @Override
    protected void setDataSet(List<JFreeChartData> oriData) {
        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
//        for (Map.Entry en :oriData.entrySet()) {
        //折线种类名
//            String lineName = String.valueOf(en.getKey());
//            //数据源
//            List<?> dataList = (List<?>) en.getValue();
        for (int i = 0; i < oriData.size(); i++) {
            JFreeChartData elem = oriData.get(i);
            dataSet.addValue(elem.getNumber(), elem.getRow(), elem.getCol());
//            }
        }
        this.dataSet = dataSet;
    }

    @Override
    protected void drawJFreeChart() {
        chart = ChartFactory.createLineChart(params.getTitle(), params.getxTitle(), params.getyTitle(),
                (CategoryDataset) this.dataSet, PlotOrientation.VERTICAL, true, true, false);
        chart.setTextAntiAlias(false);
        chart.setBackgroundPaint(Color.WHITE);
        // 设置图标题的字体重新设置title
        Font font = new Font("隶书", Font.BOLD, 25);
        TextTitle title = new TextTitle(params.getTitle());
        title.setFont(font);
        chart.setTitle(title);

        LegendTitle legend = chart.getLegend(0);
        legend.setItemFont(params.getLegendFont());
        // 设置面板字体
        Font labelFont = new Font("SansSerif", Font.TRUETYPE_FONT, 12);

        chart.setBackgroundPaint(Color.WHITE);

        CategoryPlot categoryplot = (CategoryPlot) chart.getPlot();
        // x轴 // 分类轴网格是否可见
        categoryplot.setDomainGridlinesVisible(false);
        // y轴 //数据轴网格是否可见
        categoryplot.setRangeGridlinesVisible(true);

        categoryplot.setRangeGridlinePaint(Color.black);// 虚线色彩

        categoryplot.setDomainGridlinePaint(Color.WHITE);// 虚线色彩

        categoryplot.setBackgroundPaint(Color.white);

        // 设置轴和面板之间的距离
        // categoryplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));

        CategoryAxis domainAxis = categoryplot.getDomainAxis();

        domainAxis.setLabelFont(labelFont);// 轴标题

        domainAxis.setTickLabelFont(labelFont);// 轴数值

        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45); // 横轴上的
        // Lable
        // 45度倾斜
        // 设置距离图片左端距离

        domainAxis.setLowerMargin(0.0);
        // 设置距离图片右端距离
        domainAxis.setUpperMargin(0.0);

        NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        numberaxis.setAutoRangeIncludesZero(true);

        // 获得renderer 注意这里是下嗍造型到lineandshaperenderer！！
        LineAndShapeRenderer lineandshaperenderer = (LineAndShapeRenderer) categoryplot.getRenderer();

        lineandshaperenderer.setBaseShapesVisible(true); // series 点（即数据点）可见

        lineandshaperenderer.setBaseLinesVisible(true); // series 点（即数据点）间有连线可见

    }
}
