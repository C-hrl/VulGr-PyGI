





























package com.opensymphony.xwork2.conversion;

import java.util.Map;


public interface NullHandler
{
    
    Object nullMethodResult(Map<String, Object> context, Object target, String methodName, Object[] args);
    
    
    Object nullPropertyValue(Map<String, Object> context, Object target, Object property);
}