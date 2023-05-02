
package com.opensymphony.xwork2.validator;

import java.util.List;


public interface ActionValidatorManager {

    
    List<Validator> getValidators(Class clazz, String context, String method);

    
    List<Validator> getValidators(Class clazz, String context);

    
    void validate(Object object, String context) throws ValidationException;

    
    void validate(Object object, String context, ValidatorContext validatorContext) throws ValidationException;

    
    void validate(Object object, String context, String method) throws ValidationException;

    
    void validate(Object object, String context, ValidatorContext validatorContext, String method) throws ValidationException;
}
