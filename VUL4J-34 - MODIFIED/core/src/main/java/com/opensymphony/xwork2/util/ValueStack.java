
package com.opensymphony.xwork2.util;

import java.util.Map;


public interface ValueStack {

    public static final String VALUE_STACK = "com.opensymphony.xwork2.util.ValueStack.ValueStack";

    public static final String REPORT_ERRORS_ON_NO_PROP = "com.opensymphony.xwork2.util.ValueStack.ReportErrorsOnNoProp";

    
    public abstract Map<String, Object> getContext();

    
    public abstract void setDefaultType(Class defaultType);

    
    public abstract void setExprOverrides(Map<Object, Object> overrides);

    
    public abstract Map<Object, Object> getExprOverrides();

    
    public abstract CompoundRoot getRoot();

    
    public abstract void setValue(String expr, Object value);

    
    void setParameter(String expr, Object value);

    
    public abstract void setValue(String expr, Object value, boolean throwExceptionOnFailure);

    public abstract String findString(String expr);
    public abstract String findString(String expr, boolean throwExceptionOnFailure);

    
    public abstract Object findValue(String expr);

    public abstract Object findValue(String expr, boolean throwExceptionOnFailure);

    
    public abstract Object findValue(String expr, Class asType);
    public abstract Object findValue(String expr, Class asType,  boolean throwExceptionOnFailure);

    
    public abstract Object peek();

    
    public abstract Object pop();

    
    public abstract void push(Object o);

    
    public abstract void set(String key, Object o);

    
    public abstract int size();

}