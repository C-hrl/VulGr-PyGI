

package org.apache.struts2.config;

import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;



class PropertiesSettings implements Settings {

    private static final Logger LOG = LogManager.getLogger(PropertiesSettings.class);

    private LocatableProperties settings;

    
    public PropertiesSettings(String name) {
        
        URL settingsUrl = ClassLoaderUtil.getResource(name + ".properties", getClass());
        
        if (settingsUrl == null) {
            LOG.debug("{}.properties missing", name);
            settings = new LocatableProperties();
            return;
        }
        
        settings = new LocatableProperties(new LocationImpl(null, settingsUrl.toString()));

        
        try (InputStream in = settingsUrl.openStream()) {
            settings.load(in);
        } catch (IOException e) {
            throw new StrutsException("Could not load " + name + ".properties: " + e, e);
        }
    }


    
    public String get(String aName) throws IllegalArgumentException {
        return settings.getProperty(aName);
    }
    
    
    public Location getLocation(String aName) throws IllegalArgumentException {
        return settings.getPropertyLocation(aName);
    }

    
    public Iterator list() {
        return settings.keySet().iterator();
    }

}
