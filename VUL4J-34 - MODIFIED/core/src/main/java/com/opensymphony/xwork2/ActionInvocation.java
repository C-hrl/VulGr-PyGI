
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;

import java.io.Serializable;



public interface ActionInvocation extends Serializable {

    
    Object getAction();

    
    boolean isExecuted();

    
    ActionContext getInvocationContext();

    
    ActionProxy getProxy();

    
    Result getResult() throws Exception;

    
    String getResultCode();

    
    void setResultCode(String resultCode);

    
    ValueStack getStack();

    
    void addPreResultListener(PreResultListener listener);

    
    String invoke() throws Exception;

    
    String invokeActionOnly() throws Exception;

    
    void setActionEventListener(ActionEventListener listener);

    void init(ActionProxy proxy) ;

    
    ActionInvocation serialize();

    
    ActionInvocation deserialize(ActionContext actionContext);

}
