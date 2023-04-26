package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.conversion.TypeConverter;

import java.util.Map;


public interface ConverterFactory {

    
    TypeConverter buildConverter(Class<? extends TypeConverter> converterClass, Map<String, Object> extraContext) throws Exception;

}
