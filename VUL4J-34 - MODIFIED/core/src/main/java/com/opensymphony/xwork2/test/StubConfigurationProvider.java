package com.opensymphony.xwork2.test;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;

public class StubConfigurationProvider implements ConfigurationProvider {

    public void destroy() {
        

    }

    public void init(Configuration configuration) throws ConfigurationException {
        
    }

    public void loadPackages() throws ConfigurationException {
        

    }

    public boolean needsReload() {
        
        return false;
    }

    public void register(ContainerBuilder builder, LocatableProperties props)
            throws ConfigurationException {
        

    }

}
