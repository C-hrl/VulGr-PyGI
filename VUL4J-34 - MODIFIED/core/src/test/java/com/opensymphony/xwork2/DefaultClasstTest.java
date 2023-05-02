

package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;


public class DefaultClasstTest extends XWorkTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        
        XmlConfigurationProvider configurationProvider = new XmlConfigurationProvider("xwork-sample.xml");
        container.inject(configurationProvider);
        loadConfigurationProviders(configurationProvider);
    }

    public void testWildCardEvaluation() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy("Abstract-crud", "edit", null, null);
        assertEquals("com.opensymphony.xwork2.SimpleAction", proxy.getConfig().getClassName());
        
        proxy = actionProxyFactory.createActionProxy("/example", "edit", null, null);
        assertEquals("com.opensymphony.xwork2.ModelDrivenAction", proxy.getConfig().getClassName());
         

        proxy = actionProxyFactory.createActionProxy("/example2", "override", null, null);
        assertEquals("com.opensymphony.xwork2.ModelDrivenAction", proxy.getConfig().getClassName());
        
        proxy = actionProxyFactory.createActionProxy("/example2/subItem", "save", null, null);
        assertEquals("com.opensymphony.xwork2.ModelDrivenAction", proxy.getConfig().getClassName());
        
        proxy = actionProxyFactory.createActionProxy("/example2", "list", null, null);
        assertEquals("com.opensymphony.xwork2.ModelDrivenAction", proxy.getConfig().getClassName());
        
        proxy = actionProxyFactory.createActionProxy("/example3", "list", null, null);
        assertEquals("com.opensymphony.xwork2.SimpleAction", proxy.getConfig().getClassName());
    }

}
