
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.entities.ActionConfig;

import java.util.List;


public interface UnknownHandlerManager {

    Result handleUnknownResult(ActionContext actionContext, String actionName, ActionConfig actionConfig, String resultCode);

    
    Object handleUnknownMethod(Object action, String methodName) throws NoSuchMethodException;

    ActionConfig handleUnknownAction(String namespace, String actionName);

    boolean hasUnknownHandlers();

    List<UnknownHandler> getUnknownHandlers();

}
