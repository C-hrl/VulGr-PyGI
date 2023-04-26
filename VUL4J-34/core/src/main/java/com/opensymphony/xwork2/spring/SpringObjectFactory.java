
package com.opensymphony.xwork2.spring;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;


public class SpringObjectFactory extends ObjectFactory implements ApplicationContextAware {
    private static final Logger LOG = LogManager.getLogger(SpringObjectFactory.class);

    protected ApplicationContext appContext;
    protected AutowireCapableBeanFactory autoWiringFactory;
    protected int autowireStrategy = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;
    private final Map<String, Object> classes = new HashMap<>();
    private boolean useClassCache = true;
    private boolean alwaysRespectAutowireStrategy = false;
    
    private boolean enableAopSupport = false;

    @Inject(value="applicationContextPath",required=false)
    public void setApplicationContextPath(String ctx) {
        if (ctx != null) {
            setApplicationContext(new ClassPathXmlApplicationContext(ctx));
        }
    }

    @Inject(value = "enableAopSupport", required = false)
    public void setEnableAopSupport(String enableAopSupport) {
        this.enableAopSupport = BooleanUtils.toBoolean(enableAopSupport);
    }

    
    public void setApplicationContext(ApplicationContext appContext) throws BeansException {
        this.appContext = appContext;
        autoWiringFactory = findAutoWiringBeanFactory(this.appContext);
    }

    
    public void setAutowireStrategy(int autowireStrategy) {
        switch (autowireStrategy) {
            case AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT:
                LOG.info("Setting autowire strategy to autodetect");
                this.autowireStrategy = autowireStrategy;
                break;
            case AutowireCapableBeanFactory.AUTOWIRE_BY_NAME:
                LOG.info("Setting autowire strategy to name");
                this.autowireStrategy = autowireStrategy;
                break;
            case AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE:
                LOG.info("Setting autowire strategy to type");
                this.autowireStrategy = autowireStrategy;
                break;
            case AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR:
                LOG.info("Setting autowire strategy to constructor");
                this.autowireStrategy = autowireStrategy;
                break;
            case AutowireCapableBeanFactory.AUTOWIRE_NO:
                LOG.info("Setting autowire strategy to none");
                this.autowireStrategy = autowireStrategy;
                break;
            default:
                throw new IllegalStateException("Invalid autowire type set");
        }
    }

    public int getAutowireStrategy() {
        return autowireStrategy;
    }


    
    protected AutowireCapableBeanFactory findAutoWiringBeanFactory(ApplicationContext context) {
        if (context instanceof AutowireCapableBeanFactory) {
            
            return (AutowireCapableBeanFactory) context;
        } else if (context instanceof ConfigurableApplicationContext) {
            
            return ((ConfigurableApplicationContext) context).getBeanFactory();
        } else if (context.getParent() != null) {
            
            return findAutoWiringBeanFactory(context.getParent());
        }
        return null;
    }

    
    @Override
    public Object buildBean(String beanName, Map<String, Object> extraContext, boolean injectInternal) throws Exception {
        Object o;
        
        if (appContext.containsBean(beanName)) {
            o = appContext.getBean(beanName);
        } else {
            Class beanClazz = getClassInstance(beanName);
            o = buildBean(beanClazz, extraContext);
        }
        if (injectInternal) {
            injectInternalBeans(o);
        }
        return o;
    }

    
    @Override
    public Object buildBean(Class clazz, Map<String, Object> extraContext) throws Exception {
        Object bean;

        try {
            
            if (alwaysRespectAutowireStrategy) {
                
                bean = autoWiringFactory.createBean(clazz, autowireStrategy, false);
                injectApplicationContext(bean);
                return injectInternalBeans(bean);
            } else if (enableAopSupport) {
                bean = autoWiringFactory.createBean(clazz, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false);
                bean = autoWireBean(bean, autoWiringFactory);
                bean = autoWiringFactory.initializeBean(bean, bean.getClass().getName());
                return bean;
            } else {
                bean = autoWiringFactory.autowire(clazz, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false);
                bean = autoWiringFactory.initializeBean(bean, bean.getClass().getName());
                return autoWireBean(bean, autoWiringFactory);
            }
        } catch (UnsatisfiedDependencyException e) {
            LOG.error("Error building bean", e);
            
            return autoWireBean(super.buildBean(clazz, extraContext), autoWiringFactory);
        }
    }

    public Object autoWireBean(Object bean) {
        return autoWireBean(bean, autoWiringFactory);
    }

    
    public Object autoWireBean(Object bean, AutowireCapableBeanFactory autoWiringFactory) {
        if (autoWiringFactory != null) {
            autoWiringFactory.autowireBeanProperties(bean, autowireStrategy, false);
        }
        injectApplicationContext(bean);

        injectInternalBeans(bean);

        return bean;
    }

    private void injectApplicationContext(Object bean) {
        if (bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware) bean).setApplicationContext(appContext);
        }
    }

    public Class getClassInstance(String className) throws ClassNotFoundException {
        Class clazz = null;
        if (useClassCache) {
            synchronized(classes) {
                
                
                clazz = (Class) classes.get(className);
            }
        }

        if (clazz == null) {
            if (appContext.containsBean(className)) {
                clazz = appContext.getBean(className).getClass();
            } else {
                clazz = super.getClassInstance(className);
            }

            if (useClassCache) {
                synchronized(classes) {
                    classes.put(className, clazz);
                }
            }
        }

        return clazz;
    }

    
    @Override
    public boolean isNoArgConstructorRequired() {
        return false;
    }

    
    public void setUseClassCache(boolean useClassCache) {
        this.useClassCache = useClassCache;
    }

    
    public void setAlwaysRespectAutowireStrategy(boolean alwaysRespectAutowireStrategy) {
        this.alwaysRespectAutowireStrategy = alwaysRespectAutowireStrategy;
    }
}
