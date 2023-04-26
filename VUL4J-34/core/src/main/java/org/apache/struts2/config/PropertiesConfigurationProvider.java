

package org.apache.struts2.config;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;

import java.util.Iterator;

public class PropertiesConfigurationProvider implements ConfigurationProvider {

    public void destroy() {
    }

    public void init(Configuration configuration) throws ConfigurationException {
    }

    public void loadPackages() throws ConfigurationException {
    }

    public boolean needsReload() {
        return false;
    }

    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
        final DefaultSettings settings = new DefaultSettings();
        loadSettings(props, settings);
    }

    
    protected void loadSettings(LocatableProperties props, final Settings settings) {
        for (Iterator i = settings.list(); i.hasNext(); ) {
            String name = (String) i.next();
            props.setProperty(name, settings.get(name), settings.getLocation(name));
        }
    }
}
