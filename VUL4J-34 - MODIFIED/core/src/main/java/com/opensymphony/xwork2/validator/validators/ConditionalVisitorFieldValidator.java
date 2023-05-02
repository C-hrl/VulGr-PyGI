package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.validator.ValidationException;


public class ConditionalVisitorFieldValidator extends VisitorFieldValidator {

    private String expression;

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    
    @Override
    public void validate(Object object) throws ValidationException {
        if (validateExpression(object)) {
            super.validate(object);
        }
    }

    
    public boolean validateExpression(Object object) throws ValidationException {
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

        return answer;
    }

} 