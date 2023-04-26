
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.validator.ValidationException;



public class RequiredStringValidator extends FieldValidatorSupport {

    private boolean trim = true;

    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    public void setTrimExpression(String trimExpression) {
        trim = (Boolean) parse(trimExpression, Boolean.class);
    }

    public boolean isTrim() {
        return trim;
    }

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        Object value = this.getFieldValue(fieldName, object);

        if (!(value instanceof String)) {
            addFieldError(fieldName, object);
        } else {
            String s = (String) value;

            if (trim) {
                s = s.trim();
            }

            if (s.length() == 0) {
                addFieldError(fieldName, object);
            }
        }
    }

}
