
package com.opensymphony.xwork2.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Serializable;


public class ExceptionHolder implements Serializable {

    private static final long serialVersionUID = 1L;
    private Exception exception;

    
    public ExceptionHolder(Exception exception) {
        this.exception = exception;
    }

    
    public Exception getException() {
        return this.exception;
    }

    
    public String getExceptionStack() {
        String exceptionStack = null;

        if (getException() != null) {
            try (StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw)) {
                getException().printStackTrace(pw);
                exceptionStack = sw.toString();
            } catch (IOException e) {
                
            }
        }

        return exceptionStack;
    }
    
}
