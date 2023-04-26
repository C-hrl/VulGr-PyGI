package com.opensymphony.xwork2.conversion;


public interface TypeConverterCreator {

    
    TypeConverter createTypeConverter(String className) throws Exception;

}
