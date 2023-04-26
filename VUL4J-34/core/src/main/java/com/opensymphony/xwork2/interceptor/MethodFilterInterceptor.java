

package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.TextParseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Set;



public abstract class MethodFilterInterceptor extends AbstractInterceptor {
    protected transient Logger log = LogManager.getLogger(getClass());
    
    protected Set<String> excludeMethods = Collections.emptySet();
    protected Set<String> includeMethods = Collections.emptySet();

    public void setExcludeMethods(String excludeMethods) {
        this.excludeMethods = TextParseUtil.commaDelimitedStringToSet(excludeMethods);
    }
    
    public Set<String> getExcludeMethodsSet() {
    	return excludeMethods;
    }

    public void setIncludeMethods(String includeMethods) {
        this.includeMethods = TextParseUtil.commaDelimitedStringToSet(includeMethods);
    }
    
    public Set<String> getIncludeMethodsSet() {
    	return includeMethods;
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        if (applyInterceptor(invocation)) {
            return doIntercept(invocation);
        } 
        return invocation.invoke();
    }

    protected boolean applyInterceptor(ActionInvocation invocation) {
        String method = invocation.getProxy().getMethod();
        
        boolean applyMethod = MethodFilterInterceptorUtil.applyMethod(excludeMethods, includeMethods, method);
        if (!applyMethod) {
            log.debug("Skipping Interceptor... Method [{}] found in exclude list.", method);
        }
        return applyMethod;
    }
    
    
    protected abstract String doIntercept(ActionInvocation invocation) throws Exception;
    
}
