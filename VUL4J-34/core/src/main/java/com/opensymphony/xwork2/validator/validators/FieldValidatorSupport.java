
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.validator.FieldValidator;



public abstract class FieldValidatorSupport extends ValidatorSupport implements FieldValidator {

    private String fieldName;
    private String type;

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public void setValidatorType(String type) {
        this.type = type;
    }

    @Override
    public String getValidatorType() {
        return type;
    }
}
