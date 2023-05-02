
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.*;
import com.opensymphony.xwork2.config.providers.XWorkConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;


public class XWorkTestCaseHelper {

    public static ConfigurationManager setUp() throws Exception {
        ConfigurationManager configurationManager = new ConfigurationManager();
        configurationManager.addContainerProvider(new XWorkConfigurationProvider());
        Configuration config = configurationManager.getConfiguration();
        Container container = config.getContainer();
        
        
        ValueStack stack = container.getInstance(ValueStackFactory.class).createValueStack();
        stack.getContext().put(ActionContext.CONTAINER, container);
        ActionContext.setContext(new ActionContext(stack.getContext()));
    
        
        LocalizedTextUtil.reset();
        
    
        
        return configurationManager;
    }

    public static ConfigurationManager loadConfigurationProviders(ConfigurationManager configurationManager,
            ConfigurationProvider... providers) {
        try {
            tearDown(configurationManager);
        } catch (Exception e) {
            throw new RuntimeException("Cannot clean old configuration", e);
        }
        configurationManager = new ConfigurationManager();
        configurationManager.addContainerProvider(new ContainerProvider() {
            public void destroy() {}
            public void init(Configuration configuration) throws ConfigurationException {}
            public boolean needsReload() { return false; }

            public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
                builder.setAllowDuplicates(true);
            }
            
        });
        configurationManager.addContainerProvider(new XWorkConfigurationProvider());
        for (ConfigurationProvider prov : providers) {
            if (prov instanceof XmlConfigurationProvider) {
                ((XmlConfigurationProvider)prov).setThrowExceptionOnDuplicateBeans(false);
            }
            configurationManager.addContainerProvider(prov);
        }
        Container container = configurationManager.getConfiguration().getContainer();
        
        
        ValueStack stack = container.getInstance(ValueStackFactory.class).createValueStack();
        stack.getContext().put(ActionContext.CONTAINER, container);
        ActionContext.setContext(new ActionContext(stack.getContext()));
        
        return configurationManager;
    }

    public static void tearDown(ConfigurationManager configurationManager) throws Exception {
    
        
        if (configurationManager != null) {
            configurationManager.destroyConfiguration();
        }
        ActionContext.setContext(null);
    }
}