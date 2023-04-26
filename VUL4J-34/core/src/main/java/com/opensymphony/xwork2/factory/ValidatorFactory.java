package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.validator.Validator;

import java.util.Map;


public interface ValidatorFactory {

    
    Validator buildValidator(String className, Map<String, Object> params, Map<String, Object> extraContext) throws Exception;

}
