import com.commons.captcha.color.SingleColorFactory;
import com.commons.captcha.filter.predefined.CurvesRippleFilterFactory;
import com.commons.captcha.service.ConfigurableCaptchaService;
import com.commons.captcha.utils.encoder.EncoderHelper;

import java.awt.*;
import java.io.FileOutputStream;

/**
 * Copyright (C)
 * RunMe
 * Author: jameslinlu
 */
public class RunMe {
    public static void main(String[] args) throws Exception {
        ConfigurableCaptchaService cs = new ConfigurableCaptchaService();
        cs.setColorFactory(new SingleColorFactory(new Color(25, 60, 170)));
        cs.setFilterFactory(new CurvesRippleFilterFactory(cs.getColorFactory()));

        FileOutputStream fos = new FileOutputStream("D://patcha_demo.png");
        String code = EncoderHelper.getChallangeAndWriteImage(cs, "png", fos);
        fos.close();
        System.out.println(code);
    }
}

