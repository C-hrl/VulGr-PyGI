
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.validator.ValidationException;
import org.apache.commons.lang3.StringUtils;


public class StringLengthFieldValidator extends FieldValidatorSupport {

    private boolean trim = true;
    private int maxLength = -1;
    private int minLength = -1;

    private String maxLengthExpression;
    private String minLengthExpression;
    private String trimExpression;

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setMaxLengthExpression(String maxLengthExpression) {
        this.maxLengthExpression = maxLengthExpression;
    }

    public int getMaxLength() {
        if (StringUtils.isNotEmpty(maxLengthExpression)) {
            return (Integer) parse(maxLengthExpression, Integer.class);
        }
        return maxLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public void setMinLengthExpression(String minLengthExpression) {
        this.minLengthExpression = minLengthExpression;
    }

    public int getMinLength() {
        if (StringUtils.isNotEmpty(minLengthExpression)) {
            return (Integer) parse(minLengthExpression, Integer.class);
        }
        return minLength;
    }

    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    public void setTrimExpression(String trimExpression) {
        this.trimExpression = trimExpression;
    }

    public boolean isTrim() {
        if (StringUtils.isNotEmpty(trimExpression)) {
            return (Boolean) parse(trimExpression, Boolean.class);
        }
        return trim;
    }

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        String val = (String) getFieldValue(fieldName, object);

        if (StringUtils.isEmpty(val)) {
            
            return;
        }
        if (isTrim()) {
            val = val.trim();
            if (val.length() <= 0) {
                
                return;
            }
        }

        int minLengthToUse = getMinLength();
        int maxLengthToUse = getMaxLength();

        if ((minLengthToUse > -1) && (val.length() < minLengthToUse)) {
            addFieldError(fieldName, object);
        } else if ((maxLengthToUse > -1) && (val.length() > maxLengthToUse)) {
            addFieldError(fieldName, object);
        }
    }

}
