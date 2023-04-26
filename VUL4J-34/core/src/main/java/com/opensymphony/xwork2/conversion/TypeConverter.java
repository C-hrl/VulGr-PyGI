





























package com.opensymphony.xwork2.conversion;

import java.lang.reflect.Member;
import java.util.Map;


public interface TypeConverter
{
    
    public Object convertValue(Map<String, Object> context, Object target, Member member, String propertyName, Object value, Class toType);
    
    public static final Object NO_CONVERSION_POSSIBLE = "ognl.NoConversionPossible";
    
    public static final String TYPE_CONVERTER_CONTEXT_KEY = "_typeConverter";
}