
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.UnknownHandlerConfig;
import com.opensymphony.xwork2.inject.Container;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;



public interface Configuration extends Serializable {

    void rebuildRuntimeConfiguration();

    PackageConfig getPackageConfig(String name);

    Set<String> getPackageConfigNames();

    Map<String, PackageConfig> getPackageConfigs();

    
    RuntimeConfiguration getRuntimeConfiguration();

    void addPackageConfig(String name, PackageConfig packageConfig);

    
    PackageConfig removePackageConfig(String packageName);

    
    void destroy();

    
    List<PackageProvider> reloadContainer(List<ContainerProvider> containerProviders) throws ConfigurationException;

    
    Container getContainer();

    Set<String> getLoadedFileNames();

    
    List<UnknownHandlerConfig> getUnknownHandlerStack();

    
    void setUnknownHandlerStack(List<UnknownHandlerConfig> unknownHandlerStack);
}
