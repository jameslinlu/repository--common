/*
 * Copyright (c) 2009 Piotr Piastucki
 * 
 * This file is part of Patchca CAPTCHA library.
 * 
 *  Patchca is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Patchca is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Patchca. If not, see <http://www.gnu.org/licenses/>.
 */
package com.commons.captcha.service;

import com.commons.captcha.background.BackgroundFactory;
import com.commons.captcha.color.ColorFactory;
import com.commons.captcha.filter.FilterFactory;
import com.commons.captcha.font.FontFactory;
import com.commons.captcha.text.renderer.TextRenderer;
import com.commons.captcha.word.WordFactory;

import java.awt.image.BufferedImage;

public abstract class AbstractCaptchaService implements CaptchaService {

    //字体： 设置字体类型和大小等
    protected FontFactory fontFactory;
    //随机字符 和 长度 等
    protected WordFactory wordFactory;
    //字体颜色
    protected ColorFactory colorFactory;
    //背景颜色
    protected BackgroundFactory backgroundFactory;
    //字体渲染
    protected TextRenderer textRenderer;
    //过滤形状 曲线等等
    protected FilterFactory filterFactory;
    protected int width;
    protected int height;

    public void setFontFactory(FontFactory fontFactory) {
        this.fontFactory = fontFactory;
    }

    public void setWordFactory(WordFactory wordFactory) {
        this.wordFactory = wordFactory;
    }

    public void setColorFactory(ColorFactory colorFactory) {
        this.colorFactory = colorFactory;
    }

    public void setBackgroundFactory(BackgroundFactory backgroundFactory) {
        this.backgroundFactory = backgroundFactory;
    }

    public void setTextRenderer(TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
    }

    public void setFilterFactory(FilterFactory filterFactory) {
        this.filterFactory = filterFactory;
    }

    public FontFactory getFontFactory() {
        return fontFactory;
    }

    public WordFactory getWordFactory() {
        return wordFactory;
    }

    public ColorFactory getColorFactory() {
        return colorFactory;
    }

    public BackgroundFactory getBackgroundFactory() {
        return backgroundFactory;
    }

    public TextRenderer getTextRenderer() {
        return textRenderer;
    }

    public FilterFactory getFilterFactory() {
        return filterFactory;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public Captcha getCaptcha() {
        BufferedImage bufImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        backgroundFactory.fillBackground(bufImage);
        String word = wordFactory.getNextWord();
        textRenderer.draw(word, bufImage, fontFactory, colorFactory);
        bufImage = filterFactory.applyFilters(bufImage);
        return new Captcha(word, bufImage);
    }

}
