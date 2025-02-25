package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.conversion.TypeConverterCreator;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.XWorkTypeConverterWrapper;


public class DefaultTypeConverterCreator implements TypeConverterCreator {

    private ObjectFactory objectFactory;

    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    public TypeConverter createTypeConverter(String className) throws Exception {
        
        Object obj = objectFactory.buildBean(className, null);
        if (obj instanceof TypeConverter) {
            return (TypeConverter) obj;

            
        } else if (obj instanceof ognl.TypeConverter) {
            return new XWorkTypeConverterWrapper((ognl.TypeConverter) obj);
        } else {
            throw new IllegalArgumentException("Type converter class " + obj.getClass() + " doesn't implement com.opensymphony.xwork2.conversion.TypeConverter");
        }
    }

}
