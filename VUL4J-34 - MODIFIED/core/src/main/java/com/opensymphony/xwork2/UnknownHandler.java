
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.entities.ActionConfig;


public interface UnknownHandler {
    
    
    ActionConfig handleUnknownAction(String namespace, String actionName) throws XWorkException;
    
    
    Result handleUnknownResult(ActionContext actionContext, String actionName, ActionConfig actionConfig, String resultCode) throws XWorkException;
    
    
	Object handleUnknownActionMethod(Object action, String methodName);

}
