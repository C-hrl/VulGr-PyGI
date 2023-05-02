
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.Validateable;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.opensymphony.xwork2.interceptor.PrefixMethodInvocationUtil;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ValidationInterceptor extends MethodFilterInterceptor {

    private boolean validateAnnotatedMethodOnly;
    
    private ActionValidatorManager actionValidatorManager;
    
    private static final Logger LOG = LogManager.getLogger(ValidationInterceptor.class);
    
    private final static String VALIDATE_PREFIX = "validate";
    private final static String ALT_VALIDATE_PREFIX = "validateDo";
    
    private boolean alwaysInvokeValidate = true;
    private boolean programmatic = true;
    private boolean declarative = true;

    @Inject
    public void setActionValidatorManager(ActionValidatorManager mgr) {
        this.actionValidatorManager = mgr;
    }
    
    
    public void setProgrammatic(boolean programmatic) {
        this.programmatic = programmatic;
    }

    
    public void setDeclarative(boolean declarative) {
        this.declarative = declarative;
    }

    
    public void setAlwaysInvokeValidate(String alwaysInvokeValidate) {
            this.alwaysInvokeValidate = Boolean.parseBoolean(alwaysInvokeValidate);
    }

    
    public boolean isValidateAnnotatedMethodOnly() {
        return validateAnnotatedMethodOnly;
    }

    
    public void setValidateAnnotatedMethodOnly(boolean validateAnnotatedMethodOnly) {
        this.validateAnnotatedMethodOnly = validateAnnotatedMethodOnly;
    }

    
    protected void doBeforeInvocation(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        ActionProxy proxy = invocation.getProxy();

        
        
        
        String context = this.getValidationContext(proxy);
        String method = proxy.getMethod();

        if (log.isDebugEnabled()) {
            log.debug("Validating {}/{} with method {}.", invocation.getProxy().getNamespace(), invocation.getProxy().getActionName(), method);
        }
        

        if (declarative) {
           if (validateAnnotatedMethodOnly) {
               actionValidatorManager.validate(action, context, method);
           } else {
               actionValidatorManager.validate(action, context);
           }
       }    
        
        if (action instanceof Validateable && programmatic) {
            
            Exception exception = null; 
            
            Validateable validateable = (Validateable) action;
            LOG.debug("Invoking validate() on action {}", validateable);

            try {
                PrefixMethodInvocationUtil.invokePrefixMethod(invocation, new String[]{VALIDATE_PREFIX, ALT_VALIDATE_PREFIX});
            }
            catch(Exception e) {
                
                
                LOG.warn("an exception occured while executing the prefix method", e);
                exception = e;
            }
            
            
            if (alwaysInvokeValidate) {
                validateable.validate();
            }
            
            if (exception != null) { 
                
                throw exception;
            }
        }
    }

    @Override
    protected String doIntercept(ActionInvocation invocation) throws Exception {
        doBeforeInvocation(invocation);
        return invocation.invoke();
    }
    
    
    protected String getValidationContext(ActionProxy proxy) {
        
        return proxy.getActionName();
    }

}
