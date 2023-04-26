
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;

import java.io.Serializable;



public interface Interceptor extends Serializable {

    
    void destroy();

    
    void init();

    
    String intercept(ActionInvocation invocation) throws Exception;

}
