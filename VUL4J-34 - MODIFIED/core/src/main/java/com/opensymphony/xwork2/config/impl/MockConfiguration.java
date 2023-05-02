
package com.opensymphony.xwork2.config.impl;

import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.config.*;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.UnknownHandlerConfig;
import com.opensymphony.xwork2.config.providers.XWorkConfigurationProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.util.location.LocatableProperties;

import java.util.*;



public class MockConfiguration implements Configuration {

    private Map<String, PackageConfig> packages = new HashMap<>();
    private Set<String> loadedFiles = new HashSet<>();
    private Container container;
    protected List<UnknownHandlerConfig> unknownHandlerStack;
    private ContainerBuilder builder;

    public MockConfiguration() {
        builder = new ContainerBuilder();
    }

    public void selfRegister() {
        
        builder.factory(Configuration.class, MockConfiguration.class, Scope.SINGLETON);
        LocatableProperties props = new LocatableProperties();
        new XWorkConfigurationProvider().register(builder, props);
        builder.constant(XWorkConstants.DEV_MODE, "false");
        builder.constant(XWorkConstants.RELOAD_XML_CONFIGURATION, "true");
        builder.constant(XWorkConstants.ENABLE_OGNL_EXPRESSION_CACHE, "true");
        container = builder.create(true);
    }

    public PackageConfig getPackageConfig(String name) {
        return packages.get(name);
    }

    public Set<String> getPackageConfigNames() {
        return packages.keySet();
    }

    public Map<String, PackageConfig> getPackageConfigs() {
        return packages;
    }

    public RuntimeConfiguration getRuntimeConfiguration() {
        throw new UnsupportedOperationException();
    }

    public void addPackageConfig(String name, PackageConfig packageContext) {
        packages.put(name, packageContext);
    }

    public void buildRuntimeConfiguration() {
        throw new UnsupportedOperationException();
    }

    public void destroy() {
        throw new UnsupportedOperationException();
    }

    public void rebuildRuntimeConfiguration() {
        throw new UnsupportedOperationException();
    }

    public PackageConfig removePackageConfig(String name) {
        return packages.remove(name);
    }

    public Container getContainer() {
        return container;
    }

    public Set<String> getLoadedFileNames() {
        return loadedFiles;
    }

    public List<PackageProvider> reloadContainer(
            List<ContainerProvider> containerProviders)
            throws ConfigurationException {
        throw new UnsupportedOperationException();
    }

    public List<UnknownHandlerConfig> getUnknownHandlerStack() {
        return unknownHandlerStack;
    }

    public void setUnknownHandlerStack(List<UnknownHandlerConfig> unknownHandlerStack) {
        this.unknownHandlerStack = unknownHandlerStack;
    }

}
