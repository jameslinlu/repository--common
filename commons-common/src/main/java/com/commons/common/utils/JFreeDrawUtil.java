package com.commons.common.utils;

import com.commons.common.utils.jfreechart.*;
import com.commons.metadata.exception.ServiceException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: 画图工具类：饼图，圆柱图，折线图</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2016 zhong-ying.com Inc.
 * All right reserved.</p>
 *
 * @author: 许志成 on 2016/6/8.
 */
public class JFreeDrawUtil {
    /**
     * 圆柱图保存为文件格式
     *
     * @param oriData  具体数据源
     * @param filePath 保存文件路径包含文件名（如：/xx/xx/aa.png）
     * @param params   定义参数 JFreeChart配置参数
     */
    public static void drawBarAsFile(List<JFreeChartData> oriData, String filePath, JFreeChartParameters params) throws ServiceException {
        if (null == oriData) {
            throw new ServiceException("drawBar error case by oriData is null");
        }
        //JFreeChartDraw 绘制图像模板类
        JFreeChartDraw<List<JFreeChartData>> bar = new JFreeChartDrawBar();
        OutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
            bar.process(params, oriData, os);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null)
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }


    /**
     * 饼图保存为文件格式
     *
     * @param oriData
     * @param filePath
     * @param params
     */
    public static void drawPieAsFile(Map<String, Integer> oriData, String filePath, JFreeChartParameters params) throws ServiceException {
        if (null == oriData) {
            throw new ServiceException("drawBar error case by oriData is null");
        }
        JFreeChartDraw<Map<String, ?>> pie = new JFreeChartDrawPie();
        OutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
            pie.process(params, oriData, os);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null)
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * 折线图保存为文件格式
     *
     * @param oriData
     * @param filePath
     * @param params
     */
    public static void drawLineAsFile(List<JFreeChartData> oriData, String filePath, JFreeChartParameters params) throws ServiceException {
        if (null == oriData) {
            throw new ServiceException("drawBar error case by oriData is null");
        }
        JFreeChartDraw<List<JFreeChartData>> line = new JFreeChartDrawLine();
        OutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
            line.process(params, oriData, os);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null)
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
