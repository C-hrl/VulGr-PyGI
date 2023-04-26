
package com.opensymphony.xwork2.interceptor;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public interface ValidationAware {

    
    void setActionErrors(Collection<String> errorMessages);

    
    Collection<String> getActionErrors();

    
    void setActionMessages(Collection<String> messages);

    
    Collection<String> getActionMessages();

    
    void setFieldErrors(Map<String, List<String>> errorMap);

    
    Map<String, List<String>> getFieldErrors();

    
    void addActionError(String anErrorMessage);

    
    void addActionMessage(String aMessage);

    
    void addFieldError(String fieldName, String errorMessage);

    
    boolean hasActionErrors();

    
    boolean hasActionMessages();

    
    boolean hasErrors();

    
    boolean hasFieldErrors();

}
