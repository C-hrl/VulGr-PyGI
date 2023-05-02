
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;



public class XmlConfigurationProviderInvalidFileTest extends ConfigurationTestBase {

    public void testInvalidFileThrowsException() {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-invalid-file.xml";

        try {
            ConfigurationProvider provider = buildConfigurationProvider(filename);
            fail();
        } catch (ConfigurationException e) {
            
        }
    }
}
