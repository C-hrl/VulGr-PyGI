package com.opensymphony.xwork2.validator;


public interface ValidatorFactory {

    
    Validator getValidator(ValidatorConfig cfg);

    
    void registerValidator(String name, String className);

    
    String lookupRegisteredValidatorType(String name);
}
