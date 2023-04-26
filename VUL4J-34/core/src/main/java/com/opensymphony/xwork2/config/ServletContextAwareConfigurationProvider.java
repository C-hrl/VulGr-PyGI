
package com.opensymphony.xwork2.config;

import javax.servlet.ServletContext;


public interface ServletContextAwareConfigurationProvider extends ConfigurationProvider {

    
    void initWithContext(ServletContext servletContext);

}
