
package com.opensymphony.xwork2.config;



import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.providers.XWorkConfigurationProvider;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;

import java.util.Properties;



public class ConfigurationManagerTest extends XWorkTestCase {

    Mock configProviderMock;
    private Configuration configuration;

    public void testConfigurationReload() {
        
        configProviderMock.expectAndReturn("needsReload", Boolean.TRUE);
        configProviderMock.expect("init", C.isA(Configuration.class));
        configProviderMock.expect("register", C.ANY_ARGS);
        configProviderMock.expect("loadPackages", C.ANY_ARGS);
        configProviderMock.expect("destroy", C.ANY_ARGS);
        configProviderMock.matchAndReturn("toString", "mock");
        configuration.getContainer().getInstance(FileManagerFactory.class).getFileManager().setReloadingConfigs(true);
        configuration = configurationManager.getConfiguration();
        configProviderMock.verify();

        
        configProviderMock.expect("destroy");
    }

    public void testNoConfigurationReload() {
        configProviderMock.expectAndReturn("needsReload", Boolean.FALSE);
        
        configuration = configurationManager.getConfiguration();

        configProviderMock.verify();

        
        configProviderMock.expect("destroy");
    }

    public void testDestroyConfiguration() throws Exception {
    	class State {
    		public boolean isDestroyed1 =false;
    		public boolean isDestroyed2 =false;
    	}
    	
    	final State state = new State();
    	ConfigurationManager configurationManager = new ConfigurationManager();
    	configurationManager.addContainerProvider(new ConfigurationProvider() {
			public void destroy() { 
				throw new RuntimeException("testing testing 123");
			}
			public void init(Configuration configuration) throws ConfigurationException {
			}
			public void loadPackages() throws ConfigurationException {
			}
			public boolean needsReload() { return false;
			}
			public void register(ContainerBuilder builder, Properties props) throws ConfigurationException {
			}
			public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
			}
    	});
    	configurationManager.addContainerProvider(new ConfigurationProvider() {
			public void destroy() { 
				state.isDestroyed1 = true;
			}
			public void init(Configuration configuration) throws ConfigurationException {
			}
			public void loadPackages() throws ConfigurationException {
			}
			public boolean needsReload() { return false;
			}
			public void register(ContainerBuilder builder, Properties props) throws ConfigurationException {
			}
			public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
			}
    	});
    	configurationManager.addContainerProvider(new ConfigurationProvider() {
			public void destroy() { 
				throw new RuntimeException("testing testing 123");
			}
			public void init(Configuration configuration) throws ConfigurationException {
			}
			public void loadPackages() throws ConfigurationException {
			}
			public boolean needsReload() { return false;
			}
			public void register(ContainerBuilder builder, Properties props) throws ConfigurationException {
			}
			public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
			}
    	});
    	configurationManager.addContainerProvider(new ConfigurationProvider() {
			public void destroy() { 
				state.isDestroyed2 = true;
			}
			public void init(Configuration configuration) throws ConfigurationException {
			}
			public void loadPackages() throws ConfigurationException {
			}
			public boolean needsReload() { return false;
			}
			public void register(ContainerBuilder builder, Properties props) throws ConfigurationException {
			}
			public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
			}
    	});
    	
    	assertFalse(state.isDestroyed1);
    	assertFalse(state.isDestroyed2);
    	
    	configurationManager.clearContainerProviders();
    	
    	assertTrue(state.isDestroyed1);
    	assertTrue(state.isDestroyed2);
    }

    public void testClearConfigurationProviders() throws Exception {
        configProviderMock.expect("destroy");
        configurationManager.clearContainerProviders();
        configProviderMock.verify();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        configurationManager.destroyConfiguration();

        configProviderMock = new Mock(ConfigurationProvider.class);
        configProviderMock.matchAndReturn("equals", C.ANY_ARGS, false);

        ConfigurationProvider mockProvider = (ConfigurationProvider) configProviderMock.proxy();
        configurationManager.addContainerProvider(new XWorkConfigurationProvider());
        configurationManager.addContainerProvider(mockProvider);

        
        configProviderMock.expect("init", C.isA(Configuration.class));
        configProviderMock.expect("register", C.ANY_ARGS);
        configProviderMock.expect("loadPackages", C.ANY_ARGS);
        configProviderMock.matchAndReturn("toString", "mock");

        configuration = configurationManager.getConfiguration();
    }

    @Override
    protected void tearDown() throws Exception {
        configProviderMock.expect("destroy");
        super.tearDown();
    }

}
