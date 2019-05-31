package com.commons.common.utils;

import com.commons.common.utils.poi.Excel2007Reader;
import com.commons.common.utils.poi.ExcelHandler;
import com.commons.metadata.exception.ServiceException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class POIUtils implements Serializable {


    public static void createExcelToStream(List datas, String[] columns, String[] fields, OutputStream out) {
        SXSSFWorkbook wb = createExcel(datas, columns, fields);
        try {
            wb.write(out);
            out.close();
            wb.dispose();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String createExcelToFile(List datas, String[] columns, String[] fields, String rootPath, String filename) {
        SXSSFWorkbook wb = createExcel(datas, columns, fields);
        if (filename.endsWith(".xls")) {
            filename = filename + "x";
        }
        if (!new File(rootPath).exists()) {
            new File(rootPath).mkdirs();
        }
        filename = filename.substring(0, filename.lastIndexOf(".")) + "-" + DateUtil.getNowTime("yyyyMMddHHmmss") + filename.substring(filename.lastIndexOf("."));
        try {
            FileOutputStream out = new FileOutputStream(rootPath + File.separator + filename);
            wb.write(out);
            out.close();
            wb.dispose();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filename;
    }

    public static SXSSFWorkbook createExcel(List datas, String[] columns, String[] fields) {
        SXSSFWorkbook wb = null;
        wb = new SXSSFWorkbook(100);
        wb.setCompressTempFiles(true);
        Sheet sh = wb.createSheet();
        CellStyle cs = wb.createCellStyle();
        Font f = wb.createFont();
        f.setColor(IndexedColors.BLACK.getIndex());
        f.setBoldweight((short) 700);
        cs.setFont(f);
        cs.setAlignment((short) 2);
        Row row = null;
        Cell cell = null;
        Object rowObj = null;
        Object propertyObj = null;
        String[] fieldsArray = new String[0];
        for (int rownum = 0; rownum < datas.size(); rownum++) {
            if (rownum == 0) {
                row = sh.createRow(rownum);
                for (int cellnum = 0; cellnum < columns.length; cellnum++) {
                    cell = row.createCell(cellnum);
                    cell.setCellStyle(cs);
                    cell.setCellValue(columns[cellnum]);
//                    sh.autoSizeColumn(cellnum);
                }
            }
            row = sh.createRow(rownum + 1);
            File fileTmp = null;
            String valueTmp = "";
            for (int cellnum = 0; cellnum < fields.length; cellnum++) {
                cell = row.createCell(cellnum);
                rowObj = datas.get(rownum);
                if (fields[cellnum].startsWith("file:")) {
                    try {
                        propertyObj = PropertyUtils.getProperty(rowObj, fields[cellnum].substring(5));
                        List files = new ArrayList();
                        if (propertyObj.getClass().equals(File.class)) {
                            files.add((File) propertyObj);
                        }
                        if (propertyObj.getClass().equals(ArrayList.class)) {
                            files.addAll((List) propertyObj);
                        }
                        for (int i = 0; i < files.size(); i++) {
                            fileTmp = (File) files.get(i);
                            if ((fileTmp == null) || (!fileTmp.exists()) || (!fileTmp.canRead())) {
                                continue;
                            }
                            InputStream is = new FileInputStream(fileTmp);
                            byte[] bytes = IOUtils.toByteArray(is);
                            int pictureIdx = wb.addPicture(bytes, 5);
                            is.close();

                            ClientAnchor anchor = wb.getCreationHelper().createClientAnchor();

                            anchor.setCol1(cellnum + i);
                            anchor.setRow1(rownum + 1);
                            Picture pict = sh.createDrawingPatriarch().createPicture(anchor, pictureIdx);

                            pict.resize(0.1D);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        boolean fmtDate = false;
                        String field = fields[cellnum];
                        String pattern = "yyyy-MM-dd HH:mm:ss";
                        if (field.startsWith("date#")) {
                            //date#yyyy-MM-dd HH:mm:ss#user.time
                            fmtDate = true;
                            String[] dateParams = field.split("#");
                            field = dateParams[dateParams.length - 1];
                            if (dateParams.length == 3 && !dateParams[1].trim().equals("")) {
                                pattern = dateParams[1];
                            }
                        }

                        fieldsArray = field.split("\\.");

                        if (fieldsArray.length > 1) {
                            valueTmp = BeanUtils.getNestedProperty(rowObj, field);
                        } else {
                            valueTmp = BeanUtils.getProperty(rowObj, field);
                        }
                        try {
                            Object date = PropertyUtils.getProperty(rowObj, field);
                            if (valueTmp != null && !valueTmp.equals("") && fmtDate && date.getClass() == Date.class) {
                                valueTmp = DateFormatUtils.format((Date) date, pattern);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            valueTmp = "";
                        }
                        if (valueTmp == null) {
                            valueTmp = "";
                        }

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    cell.setCellValue(valueTmp);
                }

            }

            try {
                ((SXSSFSheet) sh).flushRows();
            } catch (IOException e) {
                e.printStackTrace();
            }

//            for (int i = 0; i < columns.length; i++) {
//                sh.autoSizeColumn(i);
//            }

            int curColWidth = 0;

            for (int i = 0; i < columns.length; i++) {
                curColWidth = sh.getColumnWidth(i);
                if (curColWidth < 4000) {
                    sh.setColumnWidth(i, 4000);
                }
            }
        }
        return wb;
    }


    public static void parseExcel2007(InputStream is, final ExcelHandler excelHandler) throws ServiceException {
        new Excel2007Reader().parser(is, excelHandler);
    }

}