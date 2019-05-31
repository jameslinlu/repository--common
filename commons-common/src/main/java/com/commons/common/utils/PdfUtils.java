package com.commons.common.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Copyright (C)
 * PdfUtils
 * Author: jameslinlu
 */
public class PdfUtils {


    public static byte[] imagePdf(byte[] image) throws Exception {
        return imagePdf(image, null, null);
    }

    /**
     * 创建图片PDF
     *
     * @param size    PDF尺寸
     * @param margins 顺序：上右下左
     * @return
     */
    public static byte[] imagePdf(byte[] image, Rectangle size, Float[] margins) throws Exception {
        if (margins == null) {
            margins = new Float[]{0f, 0f, 0f, 0f};
        }
        if (size == null) {
            size = getImageSize(image);
        }
        Image img = Image.getInstance(image, true);
        img.scaleToFit(size);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document(size, margins[3], margins[1], margins[0], margins[2]);
        PdfWriter pdf = PdfWriter.getInstance(document, byteArrayOutputStream);
        document.open();
        document.add(img);
        document.addAuthor("中盈优创");
        document.addCreator("中盈优创");
        document.close();
        pdf.close();
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 获取 [InputStream]形式 的图片大小
     *
     * @param inputStream
     * @return
     */
    public static Rectangle getImageSize(InputStream inputStream) {
        try {
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            return new Rectangle(bufferedImage.getWidth(), bufferedImage.getHeight());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Rectangle getImageSize(byte[] bytes) {
        return getImageSize(new ByteArrayInputStream(bytes));
    }
}
