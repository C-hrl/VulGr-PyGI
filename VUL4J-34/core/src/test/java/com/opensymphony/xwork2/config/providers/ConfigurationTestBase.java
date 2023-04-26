
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.impl.MockConfiguration;



public abstract class ConfigurationTestBase extends XWorkTestCase {

    protected ConfigurationProvider buildConfigurationProvider(final String filename) {
        configuration = new MockConfiguration();
        ((MockConfiguration)configuration).selfRegister();
        container = configuration.getContainer();

        XmlConfigurationProvider prov = new XmlConfigurationProvider(filename, true);
        prov.setObjectFactory(container.getInstance(ObjectFactory.class));
        prov.setFileManagerFactory(container.getInstance(FileManagerFactory.class));
        prov.init(configuration);
        prov.loadPackages();
        return prov;
    }
}
