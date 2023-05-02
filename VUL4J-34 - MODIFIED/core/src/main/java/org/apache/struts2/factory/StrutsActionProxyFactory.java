



package org.apache.struts2.factory;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.DefaultActionProxyFactory;

public class StrutsActionProxyFactory extends DefaultActionProxyFactory {

    @Override
    public ActionProxy createActionProxy(ActionInvocation inv, String namespace, String actionName, String methodName, boolean executeResult, boolean cleanupContext) {
        
        StrutsActionProxy proxy = new StrutsActionProxy(inv, namespace, actionName, methodName, executeResult, cleanupContext);
        container.inject(proxy);
        proxy.prepare();
        return proxy;
    }
}
