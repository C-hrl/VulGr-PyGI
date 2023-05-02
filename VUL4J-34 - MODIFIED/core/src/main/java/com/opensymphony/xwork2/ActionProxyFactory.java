
package com.opensymphony.xwork2;

import java.util.Map;



public interface ActionProxyFactory {

    
    ActionProxy createActionProxy(String namespace, String actionName, String methodName, Map<String, Object> extraContext);

    
    ActionProxy createActionProxy(String namespace, String actionName, String methodName, Map<String, Object> extraContext, boolean executeResult, boolean cleanupContext);


     
    ActionProxy createActionProxy(ActionInvocation actionInvocation, String namespace, String actionName, String methodName,
                                         boolean executeResult, boolean cleanupContext);
    
}
