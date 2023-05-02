
package com.opensymphony.xwork2.config;


public interface PackageProvider {
    
    
    void init(Configuration configuration) throws ConfigurationException;
    
    
    boolean needsReload();

    
    void loadPackages() throws ConfigurationException;
    
}
