
package com.opensymphony.xwork2.util.location;

import java.util.List;



public interface Location {
    
    
    public static final Location UNKNOWN = LocationImpl.UNKNOWN;
    
    
    String getDescription();
    
    
    String getURI();

    
    int getLineNumber();
    
    
    int getColumnNumber();
    
    
    List<String> getSnippet(int padding);
}
