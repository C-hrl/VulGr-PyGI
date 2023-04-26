
package com.opensymphony.xwork2.util;


public interface ValueStackFactory {

    
    ValueStack createValueStack();
    
    
    ValueStack createValueStack(ValueStack stack);
    
}
