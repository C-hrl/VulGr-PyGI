

package org.apache.struts2.config;

import com.opensymphony.xwork2.util.location.Location;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;


public class DefaultSettings implements Settings {

    private static final Logger LOG = LogManager.getLogger(DefaultSettings.class);

    
    private Settings delegate;

    
    public DefaultSettings() {

        ArrayList<Settings> list = new ArrayList<>();

        
        try {
            list.add(new PropertiesSettings("struts"));
        } catch (Exception e) {
            LOG.warn("DefaultSettings: Could not find or error in struts.properties", e);
        }

        delegate = new DelegatingSettings(list);

        
        String files = delegate.get(StrutsConstants.STRUTS_CUSTOM_PROPERTIES);
        if (files != null) {
            StringTokenizer customProperties = new StringTokenizer(files, ",");

            while (customProperties.hasMoreTokens()) {
                String name = customProperties.nextToken();
                try {
                    list.add(new PropertiesSettings(name));
                } catch (Exception e) {
                    LOG.error("DefaultSettings: Could not find {}.properties. Skipping.", name);
                }
            }

            delegate = new DelegatingSettings(list);
        }
    }

    public Location getLocation(String name) {
        return delegate.getLocation(name);
    }

    public String get(String aName) throws IllegalArgumentException {
        return delegate.get(aName);
    }

    public Iterator list() {
        return delegate.list();
    }

}
