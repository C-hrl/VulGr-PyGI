

package com.opensymphony.xwork2.spring.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.spring.SpringObjectFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;


public class ActionAutowiringInterceptor extends AbstractInterceptor implements ApplicationContextAware {
    private static final Logger LOG = LogManager.getLogger(ActionAutowiringInterceptor.class);

    public static final String APPLICATION_CONTEXT = "com.opensymphony.xwork2.spring.interceptor.ActionAutowiringInterceptor.applicationContext";

    private boolean initialized = false;
    private ApplicationContext context;
    private SpringObjectFactory factory;
    private Integer autowireStrategy;

    
    public void setAutowireStrategy(Integer autowireStrategy) {
        this.autowireStrategy = autowireStrategy;
    }

    
    @Override public String intercept(ActionInvocation invocation) throws Exception {
        if (!initialized) {
            ApplicationContext applicationContext = (ApplicationContext) ActionContext.getContext().getApplication().get(
                    WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

            if (applicationContext == null) {
                LOG.warn("ApplicationContext could not be found.  Action classes will not be autowired.");
            } else {
                setApplicationContext(applicationContext);
                factory = new SpringObjectFactory();
                factory.setApplicationContext(getApplicationContext());
                if (autowireStrategy != null) {
                    factory.setAutowireStrategy(autowireStrategy);
                }
            }
            initialized = true;
        }

        if (factory != null) {
            Object bean = invocation.getAction();
            factory.autoWireBean(bean);
    
            ActionContext.getContext().put(APPLICATION_CONTEXT, context);
        }
        return invocation.invoke();
    }

    
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    
    protected ApplicationContext getApplicationContext() {
        return context;
    }

}
