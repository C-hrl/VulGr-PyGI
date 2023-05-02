
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.validator.ValidationException;



public class FieldExpressionValidator extends FieldValidatorSupport {

    private String expression;

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();

        Boolean answer = Boolean.FALSE;
        Object obj = null;

        try {
            obj = getFieldValue(expression, object);
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            
        }

        if ((obj != null) && (obj instanceof Boolean)) {
            answer = (Boolean) obj;
        } else {
            log.warn("Got result of {} when trying to get Boolean.", obj);
        }

        if (!answer.booleanValue()) {
            addFieldError(fieldName, object);
        }
    }
}
