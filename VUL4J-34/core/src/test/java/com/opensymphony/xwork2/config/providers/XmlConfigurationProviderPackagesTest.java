
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.PackageConfig;

import java.util.List;



public class XmlConfigurationProviderPackagesTest extends ConfigurationTestBase {

    public void testBadInheritance() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-bad-inheritance.xml";
        ConfigurationProvider provider = null;
        try {
	    	provider = buildConfigurationProvider(filename);
	    	fail("Should have thrown a ConfigurationException");
	        provider.init(configuration);
	        provider.loadPackages();
        } catch (ConfigurationException e) {
        	
        }
    }

    public void testBasicPackages() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-basic-packages.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);
        provider.init(configuration);
        provider.loadPackages();

        
        PackageConfig expectedNamespacePackage = new PackageConfig.Builder("namespacepkg")
            .namespace("/namespace/set")
            .isAbstract(false)
            .build();
        PackageConfig expectedAbstractPackage = new PackageConfig.Builder("abstractpkg")
            .isAbstract(true)
            .build();

        
        assertEquals(3, configuration.getPackageConfigs().size());
        assertEquals(expectedNamespacePackage, configuration.getPackageConfig("namespacepkg"));
        assertEquals(expectedAbstractPackage, configuration.getPackageConfig("abstractpkg"));
    }

    public void testDefaultPackage() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-default-package.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);
        provider.init(configuration);
        provider.loadPackages();

        
        PackageConfig expectedPackageConfig = new PackageConfig.Builder("default").build();

        
        assertEquals(1, configuration.getPackageConfigs().size());
        assertEquals(expectedPackageConfig, configuration.getPackageConfig("default"));
    }

    public void testPackageInheritance() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-package-inheritance.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        provider.init(configuration);
        provider.loadPackages();

        
        assertEquals(5, configuration.getPackageConfigs().size());
        PackageConfig defaultPackage = configuration.getPackageConfig("default");
        assertNotNull(defaultPackage);
        assertEquals("default", defaultPackage.getName());
        PackageConfig abstractPackage = configuration.getPackageConfig("abstractPackage");
        assertNotNull(abstractPackage);
        assertEquals("abstractPackage", abstractPackage.getName());
        PackageConfig singlePackage = configuration.getPackageConfig("singleInheritance");
        assertNotNull(singlePackage);
        assertEquals("singleInheritance", singlePackage.getName());
        assertEquals(1, singlePackage.getParents().size());
        assertEquals(defaultPackage, singlePackage.getParents().get(0));
        PackageConfig multiplePackage = configuration.getPackageConfig("multipleInheritance");
        assertNotNull(multiplePackage);
        assertEquals("multipleInheritance", multiplePackage.getName());
        assertEquals(3, multiplePackage.getParents().size());
        List<PackageConfig> multipleParents = multiplePackage.getParents();
        assertTrue(multipleParents.contains(defaultPackage));
        assertTrue(multipleParents.contains(abstractPackage));
        assertTrue(multipleParents.contains(singlePackage));
        
        PackageConfig parentBelow = configuration.getPackageConfig("testParentBelow");
        assertEquals(1, parentBelow.getParents().size());
        List<PackageConfig> parentBelowParents = parentBelow.getParents();
        assertTrue(parentBelowParents.contains(multiplePackage));

        configurationManager.addContainerProvider(provider);
        configurationManager.reload();

        RuntimeConfiguration runtimeConfiguration = configurationManager.getConfiguration().getRuntimeConfiguration();
        assertNotNull(runtimeConfiguration.getActionConfig("/multiple", "default"));
        assertNotNull(runtimeConfiguration.getActionConfig("/multiple", "abstract"));
        assertNotNull(runtimeConfiguration.getActionConfig("/multiple", "single"));
        assertNotNull(runtimeConfiguration.getActionConfig("/multiple", "multiple"));
        assertNotNull(runtimeConfiguration.getActionConfig("/single", "default"));
        assertNull(runtimeConfiguration.getActionConfig("/single", "abstract"));
        assertNotNull(runtimeConfiguration.getActionConfig("/single", "single"));
        assertNull(runtimeConfiguration.getActionConfig("/single", "multiple"));
        
        assertNotNull(runtimeConfiguration.getActionConfig("/parentBelow", "default"));
        assertNotNull(runtimeConfiguration.getActionConfig("/parentBelow", "abstract"));
        assertNotNull(runtimeConfiguration.getActionConfig("/parentBelow", "single"));
        assertNotNull(runtimeConfiguration.getActionConfig("/parentBelow", "multiple"));
        assertNotNull(runtimeConfiguration.getActionConfig("/parentBelow", "testParentBelowAction"));

    }

    public void testDefaultClassRef() throws ConfigurationException {
    	final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-defaultclassref-package.xml";
        final String hasDefaultClassRefPkgName = "hasDefaultClassRef";
        final String noDefaultClassRefPkgName = "noDefaultClassRef";
        final String testDefaultClassRef = "com.opensymphony.xwork2.ActionSupport";

    	ConfigurationProvider provider = buildConfigurationProvider(filename);
        provider.init(configuration);

        
        PackageConfig expectedDefaultClassRefPackage = new PackageConfig.Builder(hasDefaultClassRefPkgName).defaultClassRef(testDefaultClassRef).build();

        PackageConfig expectedNoDefaultClassRefPackage = new PackageConfig.Builder(noDefaultClassRefPkgName).build();

        
        assertEquals(2, configuration.getPackageConfigs().size());
        assertEquals(expectedDefaultClassRefPackage, configuration.getPackageConfig(hasDefaultClassRefPkgName));
        assertEquals(expectedNoDefaultClassRefPackage, configuration.getPackageConfig(noDefaultClassRefPkgName));
    }
}
