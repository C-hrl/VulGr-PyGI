
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.ValueStack;

import java.util.List;
import java.util.ResourceBundle;



public interface TextProvider {

    
    boolean hasKey(String key);

    
    String getText(String key);

    
    String getText(String key, String defaultValue);

    
    String getText(String key, String defaultValue, String obj);

    
    String getText(String key, List<?> args);

    
    String getText(String key, String[] args);

    
    String getText(String key, String defaultValue, List<?> args);

    
    String getText(String key, String defaultValue, String[] args);

    
    String getText(String key, String defaultValue, List<?> args, ValueStack stack);

    
    String getText(String key, String defaultValue, String[] args, ValueStack stack);

    
    ResourceBundle getTexts(String bundleName);

    
    ResourceBundle getTexts();
}
