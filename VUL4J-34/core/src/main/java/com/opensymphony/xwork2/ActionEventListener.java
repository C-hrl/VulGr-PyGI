
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.ValueStack;


public interface ActionEventListener {
    
    public Object prepare(Object action, ValueStack stack);
    
    
    public String handleException(Throwable t, ValueStack stack);
}
