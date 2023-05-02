
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.validator.ValidationException;



public class RequiredFieldValidator extends FieldValidatorSupport {

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        Object value = this.getFieldValue(fieldName, object);

        if (value == null) {
            addFieldError(fieldName, object);
        }
    }
}
