
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import com.opensymphony.xwork2.config.providers.XWorkConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



public class ConfigurationManager {

    protected static final Logger LOG = LogManager.getLogger(ConfigurationManager.class);
    protected Configuration configuration;
    protected Lock providerLock = new ReentrantLock();
    private List<ContainerProvider> containerProviders = new CopyOnWriteArrayList<>();
    private List<PackageProvider> packageProviders = new CopyOnWriteArrayList<>();
    protected String defaultFrameworkBeanName;
    private boolean providersChanged = false;
    private boolean reloadConfigs = true; 

    public ConfigurationManager() {
        this("xwork");
    }
    
    public ConfigurationManager(String name) {
        this.defaultFrameworkBeanName = name;
    }

    
    public synchronized Configuration getConfiguration() {
        if (configuration == null) {
            setConfiguration(createConfiguration(defaultFrameworkBeanName));
            try {
                configuration.reloadContainer(getContainerProviders());
            } catch (ConfigurationException e) {
                setConfiguration(null);
                throw new ConfigurationException("Unable to load configuration.", e);
            }
        } else {
            conditionalReload();
        }

        return configuration;
    }

    protected Configuration createConfiguration(String beanName) {
        return new DefaultConfiguration(beanName);
    }

    public synchronized void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    
    public List<ContainerProvider> getContainerProviders() {
        providerLock.lock();
        try {
            if (containerProviders.size() == 0) {
                containerProviders.add(new XWorkConfigurationProvider());
                containerProviders.add(new XmlConfigurationProvider("xwork.xml", false));
            }

            return containerProviders;
        } finally {
            providerLock.unlock();
        }
    }

    
    public void setContainerProviders(List<ContainerProvider> containerProviders) {
        providerLock.lock();
        try {
            this.containerProviders = new CopyOnWriteArrayList<>(containerProviders);
            providersChanged = true;
        } finally {
            providerLock.unlock();
        }
    }

    
    public void addContainerProvider(ContainerProvider provider) {
        if (!containerProviders.contains(provider)) {
            containerProviders.add(provider);
            providersChanged = true;
        }
    }

    public void clearContainerProviders() {
        for (ContainerProvider containerProvider : containerProviders) {
            clearContainerProvider(containerProvider);
        }
        containerProviders.clear();
        providersChanged = true;
    }

    private void clearContainerProvider(ContainerProvider containerProvider) {
        try {
            containerProvider.destroy();
        } catch (Exception e) {
            LOG.warn("Error while destroying container provider [{}]", containerProvider.toString(), e);
        }
    }

    
    public synchronized void destroyConfiguration() {
        clearContainerProviders(); 
        containerProviders = new CopyOnWriteArrayList<ContainerProvider>();
        if (configuration != null)
            configuration.destroy(); 
        configuration = null;
    }


    
    public synchronized void conditionalReload() {
        if (reloadConfigs || providersChanged) {
            LOG.debug("Checking ConfigurationProviders for reload.");
            List<ContainerProvider> providers = getContainerProviders();
            boolean reload = needReloadContainerProviders(providers);
            if (!reload) {
                reload = needReloadPackageProviders();
            }
            if (reload) {
                reloadProviders(providers);
            }
            updateReloadConfigsFlag();
            providersChanged = false;
        }
    }

    private void updateReloadConfigsFlag() {
        reloadConfigs = Boolean.parseBoolean(configuration.getContainer().getInstance(String.class, XWorkConstants.RELOAD_XML_CONFIGURATION));
        if (LOG.isDebugEnabled()) {
            LOG.debug("Updating [{}], current value is [{}], new value [{}]",
                    XWorkConstants.RELOAD_XML_CONFIGURATION, String.valueOf(reloadConfigs), String.valueOf(reloadConfigs));
        }
    }

    private boolean needReloadPackageProviders() {
        if (packageProviders != null) {
            for (PackageProvider provider : packageProviders) {
                if (provider.needsReload()) {
                    LOG.info("Detected package provider [{}] needs to be reloaded. Reloading all providers.", provider);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean needReloadContainerProviders(List<ContainerProvider> providers) {
        for (ContainerProvider provider : providers) {
            if (provider.needsReload()) {
                LOG.info("Detected container provider [{}] needs to be reloaded. Reloading all providers.", provider);
                return true;
            }
        }
        return false;
    }

    private void reloadProviders(List<ContainerProvider> providers) {
        for (ContainerProvider containerProvider : containerProviders) {
            try {
                containerProvider.destroy();
            } catch (Exception e) {
                LOG.warn("error while destroying configuration provider [{}]", containerProvider, e);
            }
        }
        packageProviders = this.configuration.reloadContainer(providers);
    }

    public synchronized void reload() {
        packageProviders = getConfiguration().reloadContainer(getContainerProviders());
    }

}
