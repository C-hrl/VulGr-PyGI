package com.opensymphony.xwork2.validator;

import java.io.InputStream;
import java.util.List;
import java.util.Map;


public interface ValidatorFileParser {
    
    List<ValidatorConfig> parseActionValidatorConfigs(ValidatorFactory validatorFactory, InputStream is, String resourceName);

    
    void parseValidatorDefinitions(Map<String,String> validators, InputStream is, String resourceName);
}
