
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.XWorkException;



public class ConfigurationException extends XWorkException {

    
    public ConfigurationException() {
    }

    
    public ConfigurationException(String s) {
        super(s);
    }
    
    
    public ConfigurationException(String s, Object target) {
        super(s, target);
    }

    
    public ConfigurationException(Throwable cause) {
        super(cause);
    }
    
    
    public ConfigurationException(Throwable cause, Object target) {
        super(cause, target);
    }

    
    public ConfigurationException(String s, Throwable cause) {
        super(s, cause);
    }
    
    
    public ConfigurationException(String s, Throwable cause, Object target) {
        super(s, cause, target);
    }
}
