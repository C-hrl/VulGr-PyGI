

package com.opensymphony.xwork2.validator.validators;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.opensymphony.xwork2.validator.ValidationException;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegexFieldValidator extends FieldValidatorSupport {

    private static final Logger LOG = LogManager.getLogger(RegexFieldValidator.class);

    private String regex;
    private String regexExpression;
    private Boolean caseSensitive = true;
    private String caseSensitiveExpression = "";
    private Boolean trim = true;
    private String trimExpression = "";

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        Object value = this.getFieldValue(fieldName, object);
        
        
        String regexToUse = getRegex();
        LOG.debug("Defined regexp as [{}]", regexToUse);

        if (value == null || regexToUse == null) {
            return;
        }

        
        if (!(value instanceof String)) {
            return;
        }

        
        String str = ((String) value).trim();
        if (str.length() == 0) {
            return;
        }

        
        Pattern pattern;
        if (isCaseSensitive()) {
            pattern = Pattern.compile(regexToUse);
        } else {
            pattern = Pattern.compile(regexToUse, Pattern.CASE_INSENSITIVE);
        }

        String compare = (String) value;
        if ( isTrimed() ) {
            compare = compare.trim();
        }
        Matcher matcher = pattern.matcher( compare );

        if (!matcher.matches()) {
            addFieldError(fieldName, object);
        }
    }

    
    public String getRegex() {
        if (StringUtils.isNotEmpty(regex)) {
            return regex;
        } else if (StringUtils.isNotEmpty(regexExpression)) {
            return (String) parse(regexExpression, String.class);
        } else {
            return null;
        }
    }

    
    public void setRegex(String regex) {
        this.regex = regex;
    }

    
    public void setRegexExpression(String regexExpression) {
        this.regexExpression = regexExpression;
    }

    
    public boolean isCaseSensitive() {
        if (StringUtils.isNotEmpty(caseSensitiveExpression)) {
            return (Boolean) parse(caseSensitiveExpression, Boolean.class);
        }
        return caseSensitive;
    }

    
    public void setCaseSensitive(Boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    
    public void setCaseSensitiveExpression(String caseSensitiveExpression) {
        this.caseSensitiveExpression = caseSensitiveExpression;
    }

    
    public boolean isTrimed() {
        if (StringUtils.isNotEmpty(trimExpression)) {
            return (Boolean) parse(trimExpression, Boolean.class);
        }
        return trim;
    }

    
    public void setTrim(Boolean trim) {
        this.trim = trim;
    }

    
    public void setTrimExpression(String trimExpression) {
        this.trimExpression = trimExpression;
    }

}
