

package org.apache.struts2.util;

import java.util.Map;

import com.opensymphony.xwork2.conversion.impl.DefaultTypeConverter;


public abstract class StrutsTypeConverter extends DefaultTypeConverter {
    public Object convertValue(Map context, Object o, Class toClass) {
        if (toClass.equals(String.class)) {
            return convertToString(context, o);
        } else if (o instanceof String[]) {
            return convertFromString(context, (String[]) o, toClass);
        } else if (o instanceof String) {
            return convertFromString(context, new String[]{(String) o}, toClass);
        } else {
            return performFallbackConversion(context, o, toClass);
        }
    }

    
    protected Object performFallbackConversion(Map context, Object o, Class toClass) {
        return super.convertValue(context, o, toClass);
    }


    
    public abstract Object convertFromString(Map context, String[] values, Class toClass);

    
    public abstract String convertToString(Map context, Object o);
}
