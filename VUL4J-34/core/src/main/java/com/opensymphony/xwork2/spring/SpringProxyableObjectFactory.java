
package com.opensymphony.xwork2.spring;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SpringProxyableObjectFactory extends SpringObjectFactory {
    
    private static final Logger LOG = LogManager.getLogger(SpringProxyableObjectFactory.class);

    private List<String> skipBeanNames = new ArrayList<>();

    @Override
    public Object buildBean(String beanName, Map<String, Object> extraContext) throws Exception {
        LOG.debug("Building bean for name {}", beanName);
        if (!skipBeanNames.contains(beanName)) {
            ApplicationContext anAppContext = getApplicationContext(extraContext);
            try {
                LOG.debug("Trying the application context... appContext = {},\n bean name = {}", anAppContext, beanName);
                return anAppContext.getBean(beanName);
            } catch (NoSuchBeanDefinitionException e) {
                LOG.debug("Did not find bean definition for bean named {}, creating a new one...", beanName);
                if (autoWiringFactory instanceof BeanDefinitionRegistry) {
                    try {
                        Class clazz = Class.forName(beanName);
                        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) autoWiringFactory;
                        RootBeanDefinition def = new RootBeanDefinition(clazz, autowireStrategy, true);
                        def.setScope(BeanDefinition.SCOPE_SINGLETON);
                        LOG.debug("Registering a new bean definition for class {}", beanName);
                        registry.registerBeanDefinition(beanName,def);
                        try {
                            return anAppContext.getBean(beanName);
                        } catch (NoSuchBeanDefinitionException e2) {
                            LOG.warn("Could not register new bean definition for bean {}", beanName);
                            skipBeanNames.add(beanName);
                        }
                    } catch (ClassNotFoundException e1) {
                        skipBeanNames.add(beanName);
                    }
                }
            }
        }
        LOG.debug("Returning autowired instance created by default ObjectFactory");
        return autoWireBean(super.buildBean(beanName, extraContext), autoWiringFactory);
    }

    
    protected ApplicationContext getApplicationContext(Map<String, Object> context) {
        return appContext;
    }
}

