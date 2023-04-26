
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;



public interface PreResultListener {

    
    void beforeResult(ActionInvocation invocation, String resultCode);

}
