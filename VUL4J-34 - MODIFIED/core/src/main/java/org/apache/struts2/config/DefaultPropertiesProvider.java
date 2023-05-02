

package org.apache.struts2.config;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;


public class DefaultPropertiesProvider extends PropertiesConfigurationProvider {

    public void destroy() {
    }

    public void init(Configuration configuration) throws ConfigurationException {
    }

    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
        try {
            PropertiesSettings defaultSettings = new PropertiesSettings("org/apache/struts2/default");
            loadSettings(props, defaultSettings);
        } catch (Exception e) {
            throw new ConfigurationException("Could not find or error in org/apache/struts2/default.properties", e);
        }
    }

}
