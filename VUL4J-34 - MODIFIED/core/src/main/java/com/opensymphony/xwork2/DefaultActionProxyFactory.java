
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

import java.util.Map;



public class DefaultActionProxyFactory implements ActionProxyFactory {

    protected Container container;
    
    public DefaultActionProxyFactory() {
        super();
    }
    
    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }
    
    public ActionProxy createActionProxy(String namespace, String actionName, Map<String, Object> extraContext) {
        return createActionProxy(namespace, actionName, null, extraContext, true, true);
    }

    public ActionProxy createActionProxy(String namespace, String actionName, String methodName, Map<String, Object> extraContext) {
        return createActionProxy(namespace, actionName, methodName, extraContext, true, true);
    }

    public ActionProxy createActionProxy(String namespace, String actionName, Map<String, Object> extraContext, boolean executeResult, boolean cleanupContext) {
        return createActionProxy(namespace, actionName, null, extraContext, executeResult, cleanupContext);
    }

    public ActionProxy createActionProxy(String namespace, String actionName, String methodName, Map<String, Object> extraContext, boolean executeResult, boolean cleanupContext) {
        
        ActionInvocation inv = createActionInvocation(extraContext, true);
        container.inject(inv);
        return createActionProxy(inv, namespace, actionName, methodName, executeResult, cleanupContext);
    }
    
    protected ActionInvocation createActionInvocation(Map<String, Object> extraContext, boolean pushAction) {
        return new DefaultActionInvocation(extraContext, pushAction);
    }
    
    public ActionProxy createActionProxy(ActionInvocation inv, String namespace, String actionName, boolean executeResult, boolean cleanupContext) {
        
        return createActionProxy(inv, namespace, actionName, null, executeResult, cleanupContext);
    }

    public ActionProxy createActionProxy(ActionInvocation inv, String namespace, String actionName, String methodName, boolean executeResult, boolean cleanupContext) {

        DefaultActionProxy proxy = new DefaultActionProxy(inv, namespace, actionName, methodName, executeResult, cleanupContext);
        container.inject(proxy);
        proxy.prepare();
        return proxy;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
