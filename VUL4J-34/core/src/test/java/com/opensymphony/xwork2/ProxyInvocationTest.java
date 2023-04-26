package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;

import java.util.HashMap;
import java.util.Map;


public class ProxyInvocationTest extends XWorkTestCase {

    
    public void testProxyInvocation() throws Exception {

        ActionProxy proxy = actionProxyFactory
            .createActionProxy("", "ProxyInvocation", null, createDummyContext());
        ActionInvocation invocation = proxy.getInvocation();
        
        String result = invocation.invokeActionOnly();
        assertEquals("proxyResult", result);

    }

    
    private Map<String, Object> createDummyContext() {
        Map<String, Object> params = new HashMap<>();
        params.put("blah", "this is blah");
        Map<String, Object> extraContext = new HashMap<>();
        extraContext.put(ActionContext.PARAMETERS, params);
        return extraContext;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        
        XmlConfigurationProvider configurationProvider = new XmlConfigurationProvider("xwork-proxyinvoke.xml");
        container.inject(configurationProvider);
        loadConfigurationProviders(configurationProvider);
    }
}
