
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.validator.ValidationException;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;


public class URLValidator extends FieldValidatorSupport {

    private String urlRegex;
    private String urlRegexExpression;

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        Object value = this.getFieldValue(fieldName, object);

        
        
        if (value == null || value.toString().length() == 0) {
            return;
        }

        if (!(value.getClass().equals(String.class)) || !Pattern.compile(getUrlRegex()).matcher((String) value).matches()) {
            addFieldError(fieldName, object);
        }
    }

    
    public String getUrlRegex() {
        if (StringUtils.isNotEmpty(urlRegexExpression)) {
            return (String) parse(urlRegexExpression, String.class);
        } else if (StringUtils.isNotEmpty(urlRegex)) {
            return urlRegex;
        } else {
            return "^(https?|ftp):\\/\\/" +
                    "(([a-z0-9$_\\.\\+!\\*\\'\\(\\),;\\?&=-]|%[0-9a-f]{2})+" +
                    "(:([a-z0-9$_\\.\\+!\\*\\'\\(\\),;\\?&=-]|%[0-9a-f]{2})+)?" +
                    "@)?(#?" +
                    ")((([a-z0-9]\\.|[a-z0-9][a-z0-9-]*[a-z0-9]\\.)*" +
                    "[a-z][a-z0-9-]*[a-z0-9]" +
                    "|((\\d|[1-9]\\d|1\\d{2}|2[0-4][0-9]|25[0-5])\\.){3}" +
                    "(\\d|[1-9]\\d|1\\d{2}|2[0-4][0-9]|25[0-5])" +
                    ")(:\\d+)?" +
                    ")(((\\/+([a-z0-9$_\\.\\+!\\*\\'\\(\\),;:@&=-]|%[0-9a-f]{2})*)*" +
                    "(\\?([a-z0-9$_\\.\\+!\\*\\'\\(\\),;:@&=-]|%[0-9a-f]{2})*)" +
                    "?)?)?" +
                    "(#([a-z0-9$_\\.\\+!\\*\\'\\(\\),;:@&=-]|%[0-9a-f]{2})*)?" +
                    "$";
        }
    }

    public void setUrlRegex(String urlRegex) {
        this.urlRegex = urlRegex;
    }

    public void setUrlRegexExpression(String urlRegexExpression) {
        this.urlRegexExpression = urlRegexExpression;
    }

}
