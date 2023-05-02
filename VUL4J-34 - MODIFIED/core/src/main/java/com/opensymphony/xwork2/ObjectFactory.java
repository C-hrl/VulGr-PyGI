
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.factory.*;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.validator.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.Map;



public class ObjectFactory implements Serializable {

    private static final Logger LOG = LogManager.getLogger(ObjectFactory.class);

    private transient ClassLoader ccl;
    private Container container;

    private ActionFactory actionFactory;
    private ResultFactory resultFactory;
    private InterceptorFactory interceptorFactory;
    private ValidatorFactory validatorFactory;
    private ConverterFactory converterFactory;
    private UnknownHandlerFactory unknownHandlerFactory;

    @Inject(value="objectFactory.classloader", required=false)
    public void setClassLoader(ClassLoader cl) {
        this.ccl = cl;
    }
    
    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    @Inject
    public void setActionFactory(ActionFactory actionFactory) {
        this.actionFactory = actionFactory;
    }

    @Inject
    public void setResultFactory(ResultFactory resultFactory) {
        this.resultFactory = resultFactory;
    }

    @Inject
    public void setInterceptorFactory(InterceptorFactory interceptorFactory) {
        this.interceptorFactory = interceptorFactory;
    }

    @Inject
    public void setValidatorFactory(ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

    @Inject
    public void setConverterFactory(ConverterFactory converterFactory) {
        this.converterFactory = converterFactory;
    }

    @Inject
    public void setUnknownHandlerFactory(UnknownHandlerFactory unknownHandlerFactory) {
        this.unknownHandlerFactory = unknownHandlerFactory;
    }

    
    public boolean isNoArgConstructorRequired() {
        return true;
    }

    
    public Class getClassInstance(String className) throws ClassNotFoundException {
        if (ccl != null) {
            return ccl.loadClass(className);
        }

        return ClassLoaderUtil.loadClass(className, this.getClass());
    }

    
    public Object buildAction(String actionName, String namespace, ActionConfig config, Map<String, Object> extraContext) throws Exception {
        return actionFactory.buildAction(actionName, namespace, config, extraContext);
    }

    
    public Object buildBean(Class clazz, Map<String, Object> extraContext) throws Exception {
        return clazz.newInstance();
    }

    
    protected Object injectInternalBeans(Object obj) {
        if (obj != null && container != null) {
            container.inject(obj);
        }
        return obj;
    }

    
    public Object buildBean(String className, Map<String, Object> extraContext) throws Exception {
        return buildBean(className, extraContext, true);
    }
    
    
    public Object buildBean(String className, Map<String, Object> extraContext, boolean injectInternal) throws Exception {
        Class clazz = getClassInstance(className);
        Object obj = buildBean(clazz, extraContext);
        if (injectInternal) {
            injectInternalBeans(obj);
        }
        return obj;
    }

    
    public Interceptor buildInterceptor(InterceptorConfig interceptorConfig, Map<String, String> interceptorRefParams) throws ConfigurationException {
        return interceptorFactory.buildInterceptor(interceptorConfig, interceptorRefParams);
    }

    
    public Result buildResult(ResultConfig resultConfig, Map<String, Object> extraContext) throws Exception {
        return resultFactory.buildResult(resultConfig, extraContext);
    }

    
    public Validator buildValidator(String className, Map<String, Object> params, Map<String, Object> extraContext) throws Exception {
        return validatorFactory.buildValidator(className, params, extraContext);
    }

    
    public TypeConverter buildConverter(Class<? extends TypeConverter> converterClass, Map<String, Object> extraContext) throws Exception {
        return converterFactory.buildConverter(converterClass, extraContext);
    }

    
    public UnknownHandler buildUnknownHandler(String unknownHandlerName, Map<String, Object> extraContext) throws Exception {
        return unknownHandlerFactory.buildUnknownHandler(unknownHandlerName, extraContext);
    }
    
}
