package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.conversion.TypeConverterHolder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class DefaultTypeConverterHolder implements TypeConverterHolder {

    
    private HashMap<String, TypeConverter> defaultMappings = new HashMap<>();  

    
    private HashMap<Class, Map<String, Object>> mappings = new HashMap<>(); 

    
    private HashSet<Class> noMapping = new HashSet<>(); 

    
    protected HashSet<String> unknownMappings = new HashSet<>();     

    public void addDefaultMapping(String className, TypeConverter typeConverter) {
        defaultMappings.put(className, typeConverter);
        if (unknownMappings.contains(className)) {
            unknownMappings.remove(className);
        }
    }

    public boolean containsDefaultMapping(String className) {
        return defaultMappings.containsKey(className);
    }

    public TypeConverter getDefaultMapping(String className) {
        return defaultMappings.get(className);
    }

    public Map<String, Object> getMapping(Class clazz) {
        return mappings.get(clazz);
    }

    public void addMapping(Class clazz, Map<String, Object> mapping) {
        mappings.put(clazz, mapping);
    }

    public boolean containsNoMapping(Class clazz) {
        return noMapping.contains(clazz);
    }

    public void addNoMapping(Class clazz) {
        noMapping.add(clazz);
    }

    public boolean containsUnknownMapping(String className) {
        return unknownMappings.contains(className);
    }

    public void addUnknownMapping(String className) {
        unknownMappings.add(className);
    }

}
