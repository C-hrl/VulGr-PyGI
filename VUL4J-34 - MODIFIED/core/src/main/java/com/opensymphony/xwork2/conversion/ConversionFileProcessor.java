package com.opensymphony.xwork2.conversion;

import java.util.Map;


public interface ConversionFileProcessor {

    
    void process(Map<String, Object> mapping, Class clazz, String converterFilename);

}
