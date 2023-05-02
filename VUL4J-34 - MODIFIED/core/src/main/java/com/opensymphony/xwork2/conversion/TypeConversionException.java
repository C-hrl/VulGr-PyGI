
package com.opensymphony.xwork2.conversion;

import com.opensymphony.xwork2.XWorkException;



public class TypeConversionException extends XWorkException {

    
    public TypeConversionException() {
    }

    
    public TypeConversionException(String s) {
        super(s);
    }

    
    public TypeConversionException(Throwable cause) {
        super(cause);
    }

    
    public TypeConversionException(String s, Throwable cause) {
        super(s, cause);
    }
}
