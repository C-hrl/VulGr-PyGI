
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.WildcardHelper;

import java.util.HashMap;
import java.util.Set;


public class MethodFilterInterceptorUtil {

	
    public static boolean applyMethod(Set<String> excludeMethods, Set<String> includeMethods, String method) {
        
        
        boolean needsPatternMatch = false;
        for (String includeMethod : includeMethods) {
            if (!"*".equals(includeMethod) && includeMethod.contains("*")) {
                needsPatternMatch = true;
                break;
            }
        }
        
        for (String excludeMethod : excludeMethods) {
            if (!"*".equals(excludeMethod) && excludeMethod.contains("*")) {
                needsPatternMatch = true;
                break;
            }
        }

        
        
        if (!needsPatternMatch && (includeMethods.contains("*") || includeMethods.size() == 0) ) {
            if (excludeMethods != null 
                    && excludeMethods.contains(method) 
                    && !includeMethods.contains(method) ) {
                return false;
            }
        }
        
        
        WildcardHelper wildcard = new WildcardHelper();
        String methodCopy ;
        if (method == null ) { 
            methodCopy = "";
        }
        else {
            methodCopy = new String(method);
        }
        for (String pattern : includeMethods) {
            if (pattern.contains("*")) {
                int[] compiledPattern = wildcard.compilePattern(pattern);
                HashMap<String, String> matchedPatterns = new HashMap<>();
                boolean matches = wildcard.match(matchedPatterns, methodCopy, compiledPattern);
                if (matches) {
                    return true; 
                }
            }
            else {
                if (pattern.equals(methodCopy)) {
                    return true; 
                }
            }
        }
        if (excludeMethods.contains("*") ) {
            return false;
        }

        
        for ( String pattern : excludeMethods) {
            if (pattern.contains("*")) {
                int[] compiledPattern = wildcard.compilePattern(pattern);
                HashMap<String, String> matchedPatterns = new HashMap<>();
                boolean matches = wildcard.match(matchedPatterns, methodCopy, compiledPattern);
                if (matches) {
                    
                    return false; 
                }
            }
            else {
                if (pattern.equals(methodCopy)) {
                    
                    return false; 
                }
            }
        }
    

        
        return includeMethods.size() == 0 || includeMethods.contains(method) || includeMethods.contains("*");
    }
    
    
    public static boolean applyMethod(String excludeMethods, String includeMethods, String method) {
    	Set<String> includeMethodsSet = TextParseUtil.commaDelimitedStringToSet(includeMethods == null? "" : includeMethods);
    	Set<String> excludeMethodsSet = TextParseUtil.commaDelimitedStringToSet(excludeMethods == null? "" : excludeMethods);
    	
    	return applyMethod(excludeMethodsSet, includeMethodsSet, method);
    }

}
