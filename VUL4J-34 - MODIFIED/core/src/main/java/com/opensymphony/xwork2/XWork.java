
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.util.Collections;
import java.util.Map;


public class XWork {
    
    ConfigurationManager configurationManager;
    
    public XWork() {
        this(new ConfigurationManager());
    }
    
    public XWork(ConfigurationManager mgr) {
        this.configurationManager = mgr;
    }
    
    public void setLoggerFactory(LoggerFactory factory) {
        LoggerFactory.setLoggerFactory(factory);
    }
    
    
    public void executeAction(String namespace, String name, String method) throws XWorkException {
        Map<String, Object> extraContext = Collections.emptyMap();
        executeAction(namespace, name, method, extraContext);
    }
    
    
    public void executeAction(String namespace, String name, String method, Map<String, Object> extraContext) throws XWorkException {
        Configuration config = configurationManager.getConfiguration();
        try {
            ActionProxy proxy = config.getContainer().getInstance(ActionProxyFactory.class).createActionProxy(
                    namespace, name, method, extraContext, true, false);
        
            proxy.execute();
        } catch (Exception e) {
            throw new XWorkException(e);
        } finally {
            ActionContext.setContext(null);
        }
    }
}
