
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Preparable;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.InvocationTargetException;



public class PrepareInterceptor extends MethodFilterInterceptor {

    private static final long serialVersionUID = -5216969014510719786L;

    private final static String PREPARE_PREFIX = "prepare";
    private final static String ALT_PREPARE_PREFIX = "prepareDo";

    private boolean alwaysInvokePrepare = true;
    private boolean firstCallPrepareDo = false;

    
    public void setAlwaysInvokePrepare(String alwaysInvokePrepare) {
        this.alwaysInvokePrepare = Boolean.parseBoolean(alwaysInvokePrepare);
    }

    
    public void setFirstCallPrepareDo(String firstCallPrepareDo) {
        this.firstCallPrepareDo = Boolean.parseBoolean(firstCallPrepareDo);
    }

    @Override
    public String doIntercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();

        if (action instanceof Preparable) {
            try {
                String[] prefixes;
                if (firstCallPrepareDo) {
                    prefixes = new String[] {ALT_PREPARE_PREFIX, PREPARE_PREFIX};
                } else {
                    prefixes = new String[] {PREPARE_PREFIX, ALT_PREPARE_PREFIX};
                }
                PrefixMethodInvocationUtil.invokePrefixMethod(invocation, prefixes);
            }
            catch (InvocationTargetException e) {
                
                Throwable cause = e.getCause();
                if (cause instanceof Exception) {
                    throw (Exception) cause;
                } else if(cause instanceof Error) {
                    throw (Error) cause;
                } else {
                    
                    throw e;
                }
            }

            if (alwaysInvokePrepare) {
                ((Preparable) action).prepare();
            }
        }

        return invocation.invoke();
    }

}
