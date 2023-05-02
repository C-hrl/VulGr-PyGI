package com.opensymphony.xwork2.util.reflection;

import com.opensymphony.xwork2.XWorkException;

public class ReflectionException extends XWorkException {

    public ReflectionException() {
        
    }

    public ReflectionException(String s) {
        super(s);
        
    }

    public ReflectionException(String s, Object target) {
        super(s, target);
        
    }

    public ReflectionException(Throwable cause) {
        super(cause);
        
    }

    public ReflectionException(Throwable cause, Object target) {
        super(cause, target);
        
    }

    public ReflectionException(String s, Throwable cause) {
        super(s, cause);
        
    }

    public ReflectionException(String s, Throwable cause, Object target) {
        super(s, cause, target);
        
    }

}
