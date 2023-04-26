

package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.mock.MockResult;


public class WildCardResultTest extends XWorkTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        
        XmlConfigurationProvider configurationProvider = new XmlConfigurationProvider("xwork-sample.xml");
        container.inject(configurationProvider);
        loadConfigurationProviders(configurationProvider);
    }

    public void testWildCardEvaluation() throws Exception {
        ActionContext.setContext(null);
        ActionProxy proxy = actionProxyFactory.createActionProxy(null, "WildCard", null, null);
        assertEquals("success", proxy.execute());
        assertEquals(VoidResult.class, proxy.getInvocation().getResult().getClass());

        ActionContext.setContext(null);
        proxy = actionProxyFactory.createActionProxy(null, "WildCardInput", null, null);
        assertEquals("input", proxy.execute());
        assertEquals(MockResult.class, proxy.getInvocation().getResult().getClass());

        ActionContext.setContext(null);
        proxy = actionProxyFactory.createActionProxy(null, "WildCardError", null, null);
        assertEquals("error", proxy.execute());
        assertEquals(MockResult.class, proxy.getInvocation().getResult().getClass());
    }

}
