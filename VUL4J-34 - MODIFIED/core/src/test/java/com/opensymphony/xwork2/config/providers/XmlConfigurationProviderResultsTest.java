
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionChainResult;
import com.opensymphony.xwork2.SimpleAction;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.mock.MockResult;

import java.util.HashMap;
import java.util.Map;



public class XmlConfigurationProviderResultsTest extends ConfigurationTestBase {

    public void testActions() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-results.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        HashMap<String, String> parameters = new HashMap<>();
        HashMap<String, ResultConfig> results = new HashMap<>();

        results.put("chainDefaultTypedResult", new ResultConfig.Builder("chainDefaultTypedResult", ActionChainResult.class.getName()).build());

        results.put("mockTypedResult", new ResultConfig.Builder("mockTypedResult", MockResult.class.getName()).build());

        Map<String, String> resultParams = new HashMap<>();
        resultParams.put("actionName", "bar.vm");
        results.put("specificLocationResult", new ResultConfig.Builder("specificLocationResult", ActionChainResult.class.getName())
                .addParams(resultParams).build());

        resultParams = new HashMap<>();
        resultParams.put("actionName", "foo.vm");
        results.put("defaultLocationResult", new ResultConfig.Builder("defaultLocationResult", ActionChainResult.class.getName())
                .addParams(resultParams).build());

        resultParams = new HashMap<>();
        resultParams.put("foo", "bar");
        results.put("noDefaultLocationResult", new ResultConfig.Builder("noDefaultLocationResult", ActionChainResult.class.getName())
                .addParams(resultParams).build());

        ActionConfig expectedAction = new ActionConfig.Builder("default", "Bar", SimpleAction.class.getName())
            .addParams(parameters)
            .addResultConfigs(results)
            .build();

        
        provider.init(configuration);
        provider.loadPackages();

        PackageConfig pkg = configuration.getPackageConfig("default");
        Map<String, ActionConfig> actionConfigs = pkg.getActionConfigs();

        
        assertEquals(1, actionConfigs.size());

        ActionConfig action = actionConfigs.get("Bar");
        assertEquals(expectedAction, action);
    }

    public void testResultInheritance() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-result-inheritance.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        
        provider.init(configuration);
        provider.loadPackages();

        
        PackageConfig subPkg = configuration.getPackageConfig("subPackage");
        assertEquals(1, subPkg.getResultTypeConfigs().size());
        assertEquals(3, subPkg.getAllResultTypeConfigs().size());
    }

    public void testResultTypes() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-results.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        
        ResultTypeConfig chainResult = new ResultTypeConfig.Builder("chain", ActionChainResult.class.getName()).build();
        ResultTypeConfig mockResult = new ResultTypeConfig.Builder("mock", MockResult.class.getName()).build();

        
        provider.init(configuration);
        provider.loadPackages();

        PackageConfig pkg = configuration.getPackageConfig("default");
        Map resultTypes = pkg.getResultTypeConfigs();

        
        assertEquals(2, resultTypes.size());
        assertEquals("chain", pkg.getDefaultResultType());
        assertEquals(chainResult, resultTypes.get("chain"));
        assertEquals(mockResult, resultTypes.get("mock"));
    }

    public void testResultNames() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-result-names.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        
        provider.init(configuration);
        provider.loadPackages();

        PackageConfig pkg = configuration.getPackageConfig("default");
        Map<String, ActionConfig> actionConfigs = pkg.getActionConfigs();
        
        
        assertNotNull(actionConfigs);
        
        Map<String, ResultConfig> resultConfigs = actionConfigs.get("noname").getResults();
        assertEquals(1, resultConfigs.size());
        assertTrue(resultConfigs.containsKey(Action.SUCCESS));
        
        resultConfigs = actionConfigs.get("success").getResults();
        assertEquals(1, resultConfigs.size());
        assertTrue(resultConfigs.containsKey(Action.SUCCESS));
        
        resultConfigs = actionConfigs.get("empty").getResults();
        assertEquals(1, resultConfigs.size());
        assertTrue(resultConfigs.containsKey(Action.SUCCESS));
        
        resultConfigs = actionConfigs.get("comma").getResults();
        assertEquals(1, resultConfigs.size());
        assertTrue(resultConfigs.containsKey(" , "));
        
        resultConfigs = actionConfigs.get("error-input").getResults();
        assertEquals(2, resultConfigs.size());
        assertTrue(resultConfigs.containsKey(Action.ERROR));
        assertTrue(resultConfigs.containsKey(Action.INPUT));
        
        resultConfigs = actionConfigs.get("error-input2").getResults();
        assertEquals(2, resultConfigs.size());
        assertTrue(resultConfigs.containsKey(Action.ERROR));
        assertTrue(resultConfigs.containsKey(Action.INPUT));
        
        resultConfigs = actionConfigs.get("noname-error-input").getResults();
        assertEquals(3, resultConfigs.size());
        assertTrue(resultConfigs.containsKey(Action.SUCCESS));
        assertTrue(resultConfigs.containsKey(Action.ERROR));
        assertTrue(resultConfigs.containsKey(Action.INPUT));
        
        resultConfigs = actionConfigs.get("noname-error-input2").getResults();
        assertEquals(3, resultConfigs.size());
        assertTrue(resultConfigs.containsKey(Action.SUCCESS));
        assertTrue(resultConfigs.containsKey(Action.ERROR));
        assertTrue(resultConfigs.containsKey(Action.INPUT));
    }
}
