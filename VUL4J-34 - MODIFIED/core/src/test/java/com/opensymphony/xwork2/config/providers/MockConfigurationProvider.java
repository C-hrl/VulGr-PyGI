
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.*;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.ModelDrivenInterceptor;
import com.opensymphony.xwork2.interceptor.ParametersInterceptor;
import com.opensymphony.xwork2.interceptor.StaticParametersInterceptor;
import com.opensymphony.xwork2.mock.MockResult;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.validator.ValidationInterceptor;

import java.util.*;



public class MockConfigurationProvider implements ConfigurationProvider {

    public static final String FOO_ACTION_NAME = "foo";
    public static final String MODEL_DRIVEN_PARAM_TEST = "modelParamTest";
    public static final String MODEL_DRIVEN_PARAM_FILTER_TEST  = "modelParamFilterTest";
    public static final String PARAM_INTERCEPTOR_ACTION_NAME = "parametersInterceptorTest";
    public static final String VALIDATION_ACTION_NAME = "validationInterceptorTest";
    public static final String VALIDATION_ALIAS_NAME = "validationAlias";
    public static final String VALIDATION_SUBPROPERTY_NAME = "subproperty";
    public static final String EXPRESSION_VALIDATION_ACTION = "expressionValidationAction";

    private static final Map<String,String> EMPTY_STRING_MAP = Collections.emptyMap();

    private Configuration configuration;
    private Map<String,String> params;
    private ObjectFactory objectFactory;

    public MockConfigurationProvider() {}
    public MockConfigurationProvider(Map<String,String> params) {
        this.params = params;
    }

    
    public void destroy() {
    }
    
    public void init(Configuration config) {
        this.configuration = config;
    }
    
    @Inject
    public void setObjectFactory(ObjectFactory fac) {
        this.objectFactory = fac;
    }

    public void loadPackages() {
        
        PackageConfig.Builder defaultPackageContext = new PackageConfig.Builder("defaultPackage");
        Map<String, String> params = new HashMap<>();
        params.put("bar", "5");

        Map<String, ResultConfig> results = new HashMap<>();
        Map<String, String> successParams = new HashMap<>();
        successParams.put("actionName", "bar");
        results.put("success", new ResultConfig.Builder("success", ActionChainResult.class.getName()).addParams(successParams).build());

        ActionConfig fooActionConfig = new ActionConfig.Builder("defaultPackage", FOO_ACTION_NAME, SimpleAction.class.getName())
            .addResultConfig(new ResultConfig.Builder(Action.ERROR, MockResult.class.getName()).build())
            .build();
        defaultPackageContext.addActionConfig(FOO_ACTION_NAME, fooActionConfig);

        results = new HashMap<>();
        successParams = new HashMap<>();
        successParams.put("actionName", "bar");
        results.put("success", new ResultConfig.Builder("success", ActionChainResult.class.getName()).addParams(successParams).build());

        List<InterceptorMapping> interceptors = new ArrayList<>();
        interceptors.add(new InterceptorMapping("params", new ParametersInterceptor()));

        ActionConfig paramInterceptorActionConfig = new ActionConfig.Builder("defaultPackage", PARAM_INTERCEPTOR_ACTION_NAME, SimpleAction.class.getName())
            .addResultConfig(new ResultConfig.Builder(Action.ERROR, MockResult.class.getName()).build())
            .addInterceptors(interceptors)
            .build();
        defaultPackageContext.addActionConfig(PARAM_INTERCEPTOR_ACTION_NAME, paramInterceptorActionConfig);

        interceptors = new ArrayList<>();
        interceptors.add(new InterceptorMapping("model", 
                objectFactory.buildInterceptor(new InterceptorConfig.Builder("model", ModelDrivenInterceptor.class.getName()).build(), EMPTY_STRING_MAP)));
        interceptors.add(new InterceptorMapping("params",
                objectFactory.buildInterceptor(new InterceptorConfig.Builder("model", ParametersInterceptor.class.getName()).build(), EMPTY_STRING_MAP)));

        ActionConfig modelParamActionConfig = new ActionConfig.Builder("defaultPackage", MODEL_DRIVEN_PARAM_TEST, ModelDrivenAction.class.getName())
            .addInterceptors(interceptors)
            .addResultConfig(new ResultConfig.Builder(Action.SUCCESS, MockResult.class.getName()).build())
            .build();
        defaultPackageContext.addActionConfig(MODEL_DRIVEN_PARAM_TEST, modelParamActionConfig);
        
        
        
        


        results = new HashMap<>();
        successParams = new HashMap<>();
        successParams.put("actionName", "bar");
        results.put("success", new ResultConfig.Builder("success", ActionChainResult.class.getName()).addParams(successParams).build());
        results.put(Action.ERROR, new ResultConfig.Builder(Action.ERROR, MockResult.class.getName()).build());

        interceptors = new ArrayList<>();
        interceptors.add(new InterceptorMapping("staticParams", 
                objectFactory.buildInterceptor(new InterceptorConfig.Builder("model", StaticParametersInterceptor.class.getName()).build(), EMPTY_STRING_MAP)));
        interceptors.add(new InterceptorMapping("model", 
                objectFactory.buildInterceptor(new InterceptorConfig.Builder("model", ModelDrivenInterceptor.class.getName()).build(), EMPTY_STRING_MAP)));
        interceptors.add(new InterceptorMapping("params", 
                objectFactory.buildInterceptor(new InterceptorConfig.Builder("model", ParametersInterceptor.class.getName()).build(), EMPTY_STRING_MAP)));
        interceptors.add(new InterceptorMapping("validation", 
                objectFactory.buildInterceptor(new InterceptorConfig.Builder("model", ValidationInterceptor.class.getName()).build(), EMPTY_STRING_MAP)));

        
        params = new HashMap<>();
        ActionConfig validationActionConfig = new ActionConfig.Builder("defaultPackage", VALIDATION_ACTION_NAME, SimpleAction.class.getName())
            .addInterceptors(interceptors)
            .addParams(params)
            .addResultConfigs(results)
            .build();
        defaultPackageContext.addActionConfig(VALIDATION_ACTION_NAME, validationActionConfig);
        defaultPackageContext.addActionConfig(VALIDATION_ALIAS_NAME,
                new ActionConfig.Builder(validationActionConfig).name(VALIDATION_ALIAS_NAME).build());
        defaultPackageContext.addActionConfig(VALIDATION_SUBPROPERTY_NAME,
                new ActionConfig.Builder(validationActionConfig).name(VALIDATION_SUBPROPERTY_NAME).build());


        params = new HashMap<>();
        ActionConfig percentageActionConfig = new ActionConfig.Builder("defaultPackage", "percentage", SimpleAction.class.getName())
                .addParams(params)
                .addResultConfigs(results)
                .addInterceptors(interceptors)
                .build();
        defaultPackageContext.addActionConfig(percentageActionConfig.getName(), percentageActionConfig);

        
        ActionConfig barActionConfig = new ActionConfig.Builder("defaultPackage", "bar", SimpleAction.class.getName())
                .addResultConfig(new ResultConfig.Builder(Action.ERROR, MockResult.class.getName()).build())
                .build();
        defaultPackageContext.addActionConfig(barActionConfig.getName(), barActionConfig);

        ActionConfig expressionValidationActionConfig = new ActionConfig.Builder("defaultPackage", EXPRESSION_VALIDATION_ACTION, SimpleAction.class.getName())
                .addInterceptors(interceptors)
                .addResultConfigs(results)
                .build();
        defaultPackageContext.addActionConfig(EXPRESSION_VALIDATION_ACTION, expressionValidationActionConfig);

        configuration.addPackageConfig("defaultPackage", defaultPackageContext.build());
    }

    
    public boolean needsReload() {
        return false;
    }

    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
        if (params != null) {
            for (String key : params.keySet()) {
                props.setProperty(key, params.get(key));
            }
        }
    }
}
