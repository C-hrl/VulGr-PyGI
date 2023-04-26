
package com.opensymphony.xwork2.util;

import java.util.Map;


public interface PatternMatcher<E extends Object> {

    
    boolean isLiteral(String pattern);

    
    E compilePattern(String data);

    
    boolean match(Map<String,String> map, String data, E expr);
    
}