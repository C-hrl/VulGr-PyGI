
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.impl.MockConfiguration;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.XWorkTestCaseHelper;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;


public class TestNGXWorkTestCase {

    protected ConfigurationManager configurationManager;
    protected Configuration configuration;
    protected Container container;
    protected ActionProxyFactory actionProxyFactory;

    @BeforeTest
    protected void setUp() throws Exception {
        configurationManager = XWorkTestCaseHelper.setUp();
        configuration = new MockConfiguration();
        ((MockConfiguration) configuration).selfRegister();
        container = configuration.getContainer();
        actionProxyFactory = container.getInstance(ActionProxyFactory.class);
    }

    @AfterTest
    protected void tearDown() throws Exception {
        XWorkTestCaseHelper.tearDown(configurationManager);
        configurationManager = null;
        configuration = null;
        container = null;
        actionProxyFactory = null;
    }

    protected void loadConfigurationProviders(ConfigurationProvider... providers) {
        configurationManager = XWorkTestCaseHelper.loadConfigurationProviders(configurationManager, providers);
        configuration = configurationManager.getConfiguration();
        container = configuration.getContainer();
        actionProxyFactory = container.getInstance(ActionProxyFactory.class);
    }
}
