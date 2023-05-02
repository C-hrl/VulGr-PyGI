
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;



public interface ContainerProvider {

    
    public void destroy();
    
    
    public void init(Configuration configuration) throws ConfigurationException;
    
    
    public boolean needsReload();
    
    
    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException;
    
}
