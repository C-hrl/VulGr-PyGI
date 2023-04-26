
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.entities.ActionConfig;



public interface ActionProxy {

    
    Object getAction();

    
    String getActionName();

    
    ActionConfig getConfig();

    
    void setExecuteResult(boolean executeResult);

    
    boolean getExecuteResult();

    
    ActionInvocation getInvocation();

    
    String getNamespace();

    
    String execute() throws Exception;

    
    String getMethod();

    
    boolean isMethodSpecified();
    
}
