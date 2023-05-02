
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;


public abstract class AbstractInterceptor implements Interceptor {

    
    public void init() {
    }
    
    
    public void destroy() {
    }


    
    public abstract String intercept(ActionInvocation invocation) throws Exception;
}
