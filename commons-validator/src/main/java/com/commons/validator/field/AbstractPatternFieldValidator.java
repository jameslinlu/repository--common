package com.commons.validator.field;

import com.alibaba.fastjson.JSON;
import com.commons.common.utils.StringUtil;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.validator.model.ValidatorScriptItem;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright (C)
 * RequireFieldValidator
 * Author: jameslinlu
 */
public abstract class AbstractPatternFieldValidator extends AbstractFieldValidator {


    private List<Pattern> patterns = new ArrayList<>();
    private String valueSeparator = null;

    public AbstractPatternFieldValidator(String field) {
        super(field);
    }

    public AbstractPatternFieldValidator(String field, List<Pattern> patterns) {
        super(field);
        this.patterns = patterns;
    }

    public void setValueSeparator(String valueSeparator) {
        this.valueSeparator = valueSeparator;
    }

    public void addPattern(Pattern patterns) {
        this.patterns.add(patterns);
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    private boolean matchPattern(String value) {
        boolean matched = false;
        //一组pattern 均无法匹配视为 验证不通过
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(value);
            if (!StringUtil.isEmpty(value) && matcher.matches()) {
                matched = true;
                break;
            }
        }
        return matched;
    }

    @Override
    public boolean isValid(Map parameters) throws ServiceException {
        List<String> values = this.getParameters(parameters);
        boolean hasUnMatch = false;
        for (String value : values) {
            if (StringUtils.isEmpty(value)) {
                continue;
            }
            if (valueSeparator != null) {
                String[] separatorValues = value.split(valueSeparator);
                for (String separatorValue : separatorValues) {
                    if (hasUnMatch = !this.matchPattern(separatorValue)) {
                        break;
                    }
                }
            }
            if (valueSeparator == null) {
                hasUnMatch = !this.matchPattern(value);
            }
            if (hasUnMatch) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ValidatorScriptItem getValidScript() throws ServiceException {
        List<String> patterns = new ArrayList<>();
        for (Pattern arg : this.getPatterns()) {
            patterns.add(arg.pattern().toString().replaceAll("\\\\","\\\\\\\\"));
        }
        StringBuffer scriptStr = new StringBuffer();
        scriptStr.append("function(value,options,form){");
        scriptStr.append("    if(!value){ return true; }   ");
        scriptStr.append("    var patterns=[];");
        for (String pattern : patterns) {
            scriptStr.append("    patterns.push('").append(pattern).append("');");
        }
        scriptStr.append("    var vals = value.split(',');");
        scriptStr.append("    for(var idx in vals){");
        scriptStr.append("        var matched=false;");
        scriptStr.append("        for(var patIdx in patterns){");
        scriptStr.append("            var regex=new RegExp(patterns[patIdx]); ");
        scriptStr.append("            if(regex.test(vals[idx])){");
        scriptStr.append("                matched=true;");
        scriptStr.append("                break;");
        scriptStr.append("            }");
        scriptStr.append("        }");
        scriptStr.append("        if(!matched){");
        scriptStr.append("            throw new Error('');");
        scriptStr.append("        }");
        scriptStr.append("        return true;");
        scriptStr.append("    }");
        scriptStr.append("}");
        ValidatorScriptItem script = new ValidatorScriptItem();
        script.setValidator(scriptStr.toString());
        script.setMessage(this.getValidatorMessage().getMessage());
        return script;
    }


}
