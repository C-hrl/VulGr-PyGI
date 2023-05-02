
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.util.ValueStack;



public interface Validator<T> {

    
    void setDefaultMessage(String message);

    
    String getDefaultMessage();

    
    String getMessage(Object object);

    
    void setMessageKey(String key);

    
    String getMessageKey();

    
    void setMessageParameters(String[] messageParameters);

    
    String[] getMessageParameters();

    
    void setValidatorContext(ValidatorContext validatorContext);

    
    ValidatorContext getValidatorContext();

    
    void validate(Object object) throws ValidationException;

    
    void setValidatorType(String type);

    
    String getValidatorType();

    
    void setValueStack(ValueStack stack);

}
