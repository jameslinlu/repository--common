package com.commons.common.utils.poi;

import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Copyright (C)
 * Excel2007Reader
 * Author: jameslinlu
 */
public class Excel2007Reader {
    /*
    public static void main(String[] args) throws Exception {
        File f = new File("G:\\文档\\云端卫士\\接口\\20160115.xlsx");
        System.out.println(f.getName());
        POIUtils.parseExcel2007(new FileInputStream(f), new ExcelHandler() {
            @Override
            public void open() {
                System.out.println("Processing");
            }

            @Override
            public void startRow(int rownum) {
                System.out.println("startRow : " + rownum);
            }

            @Override
            public void endRow(int rownum) {
                System.out.println("endRow : " + rownum);

            }

            @Override
            public void cell(String cell, String value) {
                System.out.println(cell + " : " + value);
            }

            @Override
            public void close() {
                System.out.println("Close");

            }
        });
    }

    */
    public void parser(InputStream is, final ExcelHandler excelHandler) throws ServiceException {
        OPCPackage pkg = null;
        try {
            pkg = OPCPackage.open(is);
            XSSFReader r = new XSSFReader(pkg);
            StylesTable st = r.getStylesTable();
            XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
            boolean formulasNotResults = true;
            DataFormatter dataFormatter = new DataFormatter();
            XSSFSheetXMLHandler.SheetContentsHandler sheetHandler = new XSSFSheetXMLHandler.SheetContentsHandler() {

                @Override
                public void startRow(int i) {
                    excelHandler.startRow(i);
                }

                @Override
                public void endRow(int i) {
                    excelHandler.endRow(i);
                }

                @Override
                public void cell(String s, String s1, XSSFComment xssfComment) {
                    excelHandler.cell(s, s1);
                }

                @Override
                public void headerFooter(String s, boolean b, String s1) {
                }
            };
            ContentHandler handler = new XSSFSheetXMLHandler(st, new ReadOnlySharedStringsTable(pkg), sheetHandler, dataFormatter, formulasNotResults);
            parser.setContentHandler(handler);
            Iterator<InputStream> sheets = r.getSheetsData();
            excelHandler.open();
            while (sheets.hasNext()) {
                InputStream sheet = sheets.next();
                InputSource sheetSource = new InputSource(sheet);
                parser.parse(sheetSource);
                sheet.close();
            }
            excelHandler.close();
        } catch (IOException e) {
            throw new ServiceException(ResultCode.ERROR_IO, e);
        } catch (OpenXML4JException e) {
            throw new ServiceException(ResultCode.ERROR_OPENXML4J, e);
        } catch (SAXException e) {
            throw new ServiceException(ResultCode.ERROR_SAX, e);
        } catch (ServiceException e) {
            throw e;
        } finally {
            try {
                if (pkg != null) {
                    pkg.close();
                }
                IOUtils.closeQuietly(is);
            } catch (IOException e) {
                throw new ServiceException(ResultCode.ERROR_IO, e);
            }
        }
    }
}
