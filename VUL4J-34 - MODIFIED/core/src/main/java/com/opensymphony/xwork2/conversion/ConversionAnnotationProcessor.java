package com.opensymphony.xwork2.conversion;

import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

import java.util.Map;


public interface ConversionAnnotationProcessor {

    
    void process(Map<String, Object> mapping, TypeConversion tc, String key);

}
