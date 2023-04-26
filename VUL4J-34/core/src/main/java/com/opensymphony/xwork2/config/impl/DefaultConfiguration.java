
package com.opensymphony.xwork2.config.impl;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.*;
import com.opensymphony.xwork2.config.entities.*;
import com.opensymphony.xwork2.config.providers.InterceptorBuilder;
import com.opensymphony.xwork2.conversion.*;
import com.opensymphony.xwork2.conversion.impl.*;
import com.opensymphony.xwork2.factory.*;
import com.opensymphony.xwork2.inject.*;
import com.opensymphony.xwork2.ognl.OgnlReflectionProvider;
import com.opensymphony.xwork2.ognl.OgnlUtil;
import com.opensymphony.xwork2.ognl.OgnlValueStackFactory;
import com.opensymphony.xwork2.ognl.accessor.CompoundRootAccessor;
import com.opensymphony.xwork2.util.*;
import com.opensymphony.xwork2.util.fs.DefaultFileManager;
import com.opensymphony.xwork2.util.fs.DefaultFileManagerFactory;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import ognl.PropertyAccessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;



public class DefaultConfiguration implements Configuration {

    protected static final Logger LOG = LogManager.getLogger(DefaultConfiguration.class);


    
    protected Map<String, PackageConfig> packageContexts = new LinkedHashMap<>();
    protected RuntimeConfiguration runtimeConfiguration;
    protected Container container;
    protected String defaultFrameworkBeanName;
    protected Set<String> loadedFileNames = new TreeSet<>();
    protected List<UnknownHandlerConfig> unknownHandlerStack;


    ObjectFactory objectFactory;

    public DefaultConfiguration() {
        this("xwork");
    }

    public DefaultConfiguration(String defaultBeanName) {
        this.defaultFrameworkBeanName = defaultBeanName;
    }


    public PackageConfig getPackageConfig(String name) {
        return packageContexts.get(name);
    }

    public List<UnknownHandlerConfig> getUnknownHandlerStack() {
        return unknownHandlerStack;
    }

    public void setUnknownHandlerStack(List<UnknownHandlerConfig> unknownHandlerStack) {
        this.unknownHandlerStack = unknownHandlerStack;
    }

    public Set<String> getPackageConfigNames() {
        return packageContexts.keySet();
    }

    public Map<String, PackageConfig> getPackageConfigs() {
        return packageContexts;
    }

    public Set<String> getLoadedFileNames() {
        return loadedFileNames;
    }

    public RuntimeConfiguration getRuntimeConfiguration() {
        return runtimeConfiguration;
    }

    
    public Container getContainer() {
        return container;
    }

    public void addPackageConfig(String name, PackageConfig packageContext) {
        PackageConfig check = packageContexts.get(name);
        if (check != null) {
            if (check.getLocation() != null && packageContext.getLocation() != null
                    && check.getLocation().equals(packageContext.getLocation())) {
                LOG.debug("The package name '{}' is already been loaded by the same location and could be removed: {}",
                        name, packageContext.getLocation());
            } else {
                throw new ConfigurationException("The package name '" + name
                        + "' at location "+packageContext.getLocation()
                        + " is already been used by another package at location " + check.getLocation(),
                        packageContext);
            }
        }
        packageContexts.put(name, packageContext);
    }

    public PackageConfig removePackageConfig(String packageName) {
        return packageContexts.remove(packageName);
    }

    
    public void destroy() {
        packageContexts.clear();
        loadedFileNames.clear();
    }

    public void rebuildRuntimeConfiguration() {
        runtimeConfiguration = buildRuntimeConfiguration();
    }

    
    public synchronized List<PackageProvider> reloadContainer(List<ContainerProvider> providers) throws ConfigurationException {
        packageContexts.clear();
        loadedFileNames.clear();
        List<PackageProvider> packageProviders = new ArrayList<>();

        ContainerProperties props = new ContainerProperties();
        ContainerBuilder builder = new ContainerBuilder();
        Container bootstrap = createBootstrapContainer(providers);
        for (final ContainerProvider containerProvider : providers)
        {
            bootstrap.inject(containerProvider);
            containerProvider.init(this);
            containerProvider.register(builder, props);
        }
        props.setConstants(builder);

        builder.factory(Configuration.class, new Factory<Configuration>() {
            public Configuration create(Context context) throws Exception {
                return DefaultConfiguration.this;
            }
        });

        ActionContext oldContext = ActionContext.getContext();
        try {
            

            setContext(bootstrap);
            container = builder.create(false);
            setContext(container);
            objectFactory = container.getInstance(ObjectFactory.class);

            
            for (final ContainerProvider containerProvider : providers)
            {
                if (containerProvider instanceof PackageProvider) {
                    container.inject(containerProvider);
                    ((PackageProvider)containerProvider).loadPackages();
                    packageProviders.add((PackageProvider)containerProvider);
                }
            }

            
            Set<String> packageProviderNames = container.getInstanceNames(PackageProvider.class);
            for (String name : packageProviderNames) {
                PackageProvider provider = container.getInstance(PackageProvider.class, name);
                provider.init(this);
                provider.loadPackages();
                packageProviders.add(provider);
            }

            rebuildRuntimeConfiguration();
        } finally {
            if (oldContext == null) {
                ActionContext.setContext(null);
            }
        }
        return packageProviders;
    }

    protected ActionContext setContext(Container cont) {
        ActionContext context = ActionContext.getContext();
        if (context == null) {
            ValueStack vs = cont.getInstance(ValueStackFactory.class).createValueStack();
            context = new ActionContext(vs.getContext());
            ActionContext.setContext(context);
        }
        return context;
    }

    protected Container createBootstrapContainer(List<ContainerProvider> providers) {
        ContainerBuilder builder = new ContainerBuilder();
        boolean fmFactoryRegistered = false;
        for (ContainerProvider provider : providers) {
            if (provider instanceof FileManagerProvider) {
                provider.register(builder, null);
            }
            if (provider instanceof FileManagerFactoryProvider) {
                provider.register(builder, null);
                fmFactoryRegistered = true;
            }
        }
        builder.factory(ObjectFactory.class, Scope.SINGLETON);
        builder.factory(ActionFactory.class, DefaultActionFactory.class, Scope.SINGLETON);
        builder.factory(ResultFactory.class, DefaultResultFactory.class, Scope.SINGLETON);
        builder.factory(InterceptorFactory.class, DefaultInterceptorFactory.class, Scope.SINGLETON);
        builder.factory(com.opensymphony.xwork2.factory.ValidatorFactory.class, com.opensymphony.xwork2.factory.DefaultValidatorFactory.class, Scope.SINGLETON);
        builder.factory(ConverterFactory.class, DefaultConverterFactory.class, Scope.SINGLETON);
        builder.factory(UnknownHandlerFactory.class, DefaultUnknownHandlerFactory.class, Scope.SINGLETON);

        builder.factory(FileManager.class, "system", DefaultFileManager.class, Scope.SINGLETON);
        if (!fmFactoryRegistered) {
            builder.factory(FileManagerFactory.class, DefaultFileManagerFactory.class, Scope.SINGLETON);
        }
        builder.factory(ReflectionProvider.class, OgnlReflectionProvider.class, Scope.SINGLETON);
        builder.factory(ValueStackFactory.class, OgnlValueStackFactory.class, Scope.SINGLETON);

        builder.factory(XWorkConverter.class, Scope.SINGLETON);
        builder.factory(ConversionPropertiesProcessor.class, DefaultConversionPropertiesProcessor.class, Scope.SINGLETON);
        builder.factory(ConversionFileProcessor.class, DefaultConversionFileProcessor.class, Scope.SINGLETON);
        builder.factory(ConversionAnnotationProcessor.class, DefaultConversionAnnotationProcessor.class, Scope.SINGLETON);
        builder.factory(TypeConverterCreator.class, DefaultTypeConverterCreator.class, Scope.SINGLETON);
        builder.factory(TypeConverterHolder.class, DefaultTypeConverterHolder.class, Scope.SINGLETON);

        builder.factory(XWorkBasicConverter.class, Scope.SINGLETON);
        builder.factory(TypeConverter.class, XWorkConstants.COLLECTION_CONVERTER,  CollectionConverter.class, Scope.SINGLETON);
        builder.factory(TypeConverter.class, XWorkConstants.ARRAY_CONVERTER, ArrayConverter.class, Scope.SINGLETON);
        builder.factory(TypeConverter.class, XWorkConstants.DATE_CONVERTER, DateConverter.class, Scope.SINGLETON);
        builder.factory(TypeConverter.class, XWorkConstants.NUMBER_CONVERTER,  NumberConverter.class, Scope.SINGLETON);
        builder.factory(TypeConverter.class, XWorkConstants.STRING_CONVERTER, StringConverter.class, Scope.SINGLETON);

        builder.factory(TextParser.class, OgnlTextParser.class, Scope.SINGLETON);
        builder.factory(TextProvider.class, "system", DefaultTextProvider.class, Scope.SINGLETON);

        builder.factory(ObjectTypeDeterminer.class, DefaultObjectTypeDeterminer.class, Scope.SINGLETON);
        builder.factory(PropertyAccessor.class, CompoundRoot.class.getName(), CompoundRootAccessor.class, Scope.SINGLETON);
        builder.factory(OgnlUtil.class, Scope.SINGLETON);

        builder.constant(XWorkConstants.DEV_MODE, "false");
        builder.constant(XWorkConstants.LOG_MISSING_PROPERTIES, "false");
        builder.constant(XWorkConstants.ENABLE_OGNL_EVAL_EXPRESSION, "false");
        builder.constant(XWorkConstants.ENABLE_OGNL_EXPRESSION_CACHE, "true");
        builder.constant(XWorkConstants.RELOAD_XML_CONFIGURATION, "false");

        return builder.create(true);
    }

    
    protected synchronized RuntimeConfiguration buildRuntimeConfiguration() throws ConfigurationException {
        Map<String, Map<String, ActionConfig>> namespaceActionConfigs = new LinkedHashMap<>();
        Map<String, String> namespaceConfigs = new LinkedHashMap<>();

        for (PackageConfig packageConfig : packageContexts.values()) {

            if (!packageConfig.isAbstract()) {
                String namespace = packageConfig.getNamespace();
                Map<String, ActionConfig> configs = namespaceActionConfigs.get(namespace);

                if (configs == null) {
                    configs = new LinkedHashMap<>();
                }

                Map<String, ActionConfig> actionConfigs = packageConfig.getAllActionConfigs();

                for (Object o : actionConfigs.keySet()) {
                    String actionName = (String) o;
                    ActionConfig baseConfig = actionConfigs.get(actionName);
                    configs.put(actionName, buildFullActionConfig(packageConfig, baseConfig));
                }

                namespaceActionConfigs.put(namespace, configs);
                if (packageConfig.getFullDefaultActionRef() != null) {
                    namespaceConfigs.put(namespace, packageConfig.getFullDefaultActionRef());
                }
            }
        }

        PatternMatcher<int[]> matcher = container.getInstance(PatternMatcher.class);
        return new RuntimeConfigurationImpl(Collections.unmodifiableMap(namespaceActionConfigs),
                Collections.unmodifiableMap(namespaceConfigs), matcher);
    }

    private void setDefaultResults(Map<String, ResultConfig> results, PackageConfig packageContext) {
        String defaultResult = packageContext.getFullDefaultResultType();

        for (Map.Entry<String, ResultConfig> entry : results.entrySet()) {

            if (entry.getValue() == null) {
                ResultTypeConfig resultTypeConfig = packageContext.getAllResultTypeConfigs().get(defaultResult);
                entry.setValue(new ResultConfig.Builder(null, resultTypeConfig.getClassName()).build());
            }
        }
    }

    
    private ActionConfig buildFullActionConfig(PackageConfig packageContext, ActionConfig baseConfig) throws ConfigurationException {
        Map<String, String> params = new TreeMap<>(baseConfig.getParams());
        Map<String, ResultConfig> results = new TreeMap<>();

        if (!baseConfig.getPackageName().equals(packageContext.getName()) && packageContexts.containsKey(baseConfig.getPackageName())) {
            results.putAll(packageContexts.get(baseConfig.getPackageName()).getAllGlobalResults());
        } else {
            results.putAll(packageContext.getAllGlobalResults());
        }

       	results.putAll(baseConfig.getResults());

        setDefaultResults(results, packageContext);

        List<InterceptorMapping> interceptors = new ArrayList<>(baseConfig.getInterceptors());

        if (interceptors.size() <= 0) {
            String defaultInterceptorRefName = packageContext.getFullDefaultInterceptorRef();

            if (defaultInterceptorRefName != null) {
                interceptors.addAll(InterceptorBuilder.constructInterceptorReference(new PackageConfig.Builder(packageContext), defaultInterceptorRefName,
                        new LinkedHashMap<String, String>(), packageContext.getLocation(), objectFactory));
            }
        }

        return new ActionConfig.Builder(baseConfig)
            .addParams(params)
            .addResultConfigs(results)
            .defaultClassName(packageContext.getDefaultClassRef())  
            .interceptors(interceptors)
            .addExceptionMappings(packageContext.getAllExceptionMappingConfigs())
            .build();
    }


    private static class RuntimeConfigurationImpl implements RuntimeConfiguration {

        private Map<String, Map<String, ActionConfig>> namespaceActionConfigs;
        private Map<String, ActionConfigMatcher> namespaceActionConfigMatchers;
        private NamespaceMatcher namespaceMatcher;
        private Map<String, String> namespaceConfigs;

        public RuntimeConfigurationImpl(Map<String, Map<String, ActionConfig>> namespaceActionConfigs,
                                        Map<String, String> namespaceConfigs,
                                        PatternMatcher<int[]> matcher) {
            this.namespaceActionConfigs = namespaceActionConfigs;
            this.namespaceConfigs = namespaceConfigs;

            this.namespaceActionConfigMatchers = new LinkedHashMap<>();
            this.namespaceMatcher = new NamespaceMatcher(matcher, namespaceActionConfigs.keySet());

            for (String ns : namespaceActionConfigs.keySet()) {
                namespaceActionConfigMatchers.put(ns, new ActionConfigMatcher(matcher, namespaceActionConfigs.get(ns), true));
            }
        }


        
        public ActionConfig getActionConfig(String namespace, String name) {
            ActionConfig config = findActionConfigInNamespace(namespace, name);

            
            if (config == null) {
                NamespaceMatch match = namespaceMatcher.match(namespace);
                if (match != null) {
                    config = findActionConfigInNamespace(match.getPattern(), name);

                    
                    if (config != null) {
                        config = new ActionConfig.Builder(config)
                                .addParams(match.getVariables())
                                .build();
                    }
                }
            }

            
            if (config == null && StringUtils.isNotBlank(namespace)) {
                config = findActionConfigInNamespace("", name);
            }


            return config;
        }

        private ActionConfig findActionConfigInNamespace(String namespace, String name) {
            ActionConfig config = null;
            if (namespace == null) {
                namespace = "";
            }
            Map<String, ActionConfig> actions = namespaceActionConfigs.get(namespace);
            if (actions != null) {
                config = actions.get(name);
                
                if (config == null) {
                    config = namespaceActionConfigMatchers.get(namespace).match(name);
                    
                    if (config == null) {
                        String defaultActionRef = namespaceConfigs.get(namespace);
                        if (defaultActionRef != null) {
                            config = actions.get(defaultActionRef);
                        }
                    }
                }
            }
            return config;
        }

        
        public Map<String, Map<String, ActionConfig>>  getActionConfigs() {
            return namespaceActionConfigs;
        }

        @Override
        public String toString() {
            StringBuilder buff = new StringBuilder("RuntimeConfiguration - actions are\n");

            for (String namespace : namespaceActionConfigs.keySet()) {
                Map<String, ActionConfig> actionConfigs = namespaceActionConfigs.get(namespace);

                for (String s : actionConfigs.keySet()) {
                    buff.append(namespace).append("/").append(s).append("\n");
                }
            }

            return buff.toString();
        }
    }

    class ContainerProperties extends LocatableProperties {
        private static final long serialVersionUID = -7320625750836896089L;

        @Override
        public Object setProperty(String key, String value) {
            String oldValue = getProperty(key);
            if (LOG.isInfoEnabled() && oldValue != null && !oldValue.equals(value) && !defaultFrameworkBeanName.equals(oldValue)) {
                LOG.info("Overriding property {} - old value: {} new value: {}", key, oldValue, value);
            }
            return super.setProperty(key, value);
        }

        public void setConstants(ContainerBuilder builder) {
            for (Object keyobj : keySet()) {
                String key = (String)keyobj;
                builder.factory(String.class, key, new LocatableConstantFactory<>(getProperty(key), getPropertyLocation(key)));
            }
        }
    }
}
