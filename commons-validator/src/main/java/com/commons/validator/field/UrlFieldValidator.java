package com.commons.validator.field;

import com.commons.common.utils.StringUtil;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.validator.model.ValidatorScriptItem;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Copyright (C)
 * RequireFieldValidator
 * Author: jameslinlu
 */
public class UrlFieldValidator extends AbstractPatternFieldValidator {

    private String patternUrl = "^((ht|f)tps?):\\/\\/[\\w\\-]+(\\.[\\w\\-]+)+([\\w\\-\\.,@?^=%&:\\/~\\+#]*[\\w\\-\\@?^=%&\\/~\\+#])?$";

    public UrlFieldValidator(String field) {
        super(field);
        this.addPattern(Pattern.compile(patternUrl));
    }

    @Override
    public boolean isValid(Map parameters) throws ServiceException {
        List<String> values = this.getParameters(parameters);
        for (String value : values) {
            if (!StringUtil.isEmpty(value)) {
                try {
                    new URL(value);
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String getValidatorMessageCode() {
        return ResultCode.Validator.ERROR_URL_INVALID;
    }

//    @Override
//    public ValidatorScriptItem getValidScript() throws ServiceException {
//        ValidatorScriptItem script = new ValidatorScriptItem();
//        script.setValidator("isURL");
//        script.setMessage(this.getValidatorMessage().getMessage());
//        return script;
//    }
}
