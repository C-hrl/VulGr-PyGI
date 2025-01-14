
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.ActionChainResult;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.interceptor.ParametersInterceptor;
import junit.framework.Assert;



public class XmlConfigurationProviderMultilevelTest extends ConfigurationTestBase {

    
    public void testMultiLevelInheritance() throws Exception {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-multilevel.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);
        provider.init(configuration);
        provider.loadPackages();

        
        PackageConfig packageConfig = configuration.getPackageConfig("namespace3");
        Assert.assertNotNull(packageConfig);
        assertEquals(2, packageConfig.getAllInterceptorConfigs().size());

        ActionConfig actionConfig = packageConfig.getActionConfigs().get("action3");

        assertNotNull(actionConfig);
        assertNotNull(actionConfig.getInterceptors());
        assertEquals(2, actionConfig.getInterceptors().size());
        assertEquals(ParametersInterceptor.class, ((InterceptorMapping) actionConfig.getInterceptors().get(0)).getInterceptor().getClass());
        assertNotNull(actionConfig.getResults());
        assertEquals(1, actionConfig.getResults().size());
        assertTrue(actionConfig.getResults().containsKey("success"));

        ResultConfig resultConfig = (ResultConfig) actionConfig.getResults().get("success");
        assertEquals(ActionChainResult.class.getName(), resultConfig.getClassName());
    }
}
