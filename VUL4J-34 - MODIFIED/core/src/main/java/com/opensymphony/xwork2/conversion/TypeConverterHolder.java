package com.opensymphony.xwork2.conversion;

import java.util.Map;


public interface TypeConverterHolder {

    
    void addDefaultMapping(String className, TypeConverter typeConverter);

    
    boolean containsDefaultMapping(String className);

    
    TypeConverter getDefaultMapping(String className);

    
    Map<String, Object> getMapping(Class clazz);

    
    void addMapping(Class clazz, Map<String, Object> mapping);

    
    boolean containsNoMapping(Class clazz);

    
    void addNoMapping(Class clazz);

    
    boolean containsUnknownMapping(String className);

    
    void addUnknownMapping(String className);

}
