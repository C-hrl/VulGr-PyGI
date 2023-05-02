

package org.apache.struts2.interceptor;

import java.io.Serializable;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;


public class BackgroundProcess implements Serializable {

    private static final long serialVersionUID = 3884464776311686443L;

    protected Object action;
    protected ActionInvocation invocation;
    protected String result;
    protected Exception exception;
    protected boolean done;

    
    public BackgroundProcess(String threadName, final ActionInvocation invocation, int threadPriority) {
        this.invocation = invocation;
        this.action = invocation.getAction();
        try {
            final Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        beforeInvocation();
                        result = invocation.invokeActionOnly();
                        afterInvocation();
                    } catch (Exception e) {
                        exception = e;
                    }

                    done = true;
                }
            });
            t.setName(threadName);
            t.setPriority(threadPriority);
            t.start();
        } catch (Exception e) {
            exception = e;
        }
    }

    
    protected void beforeInvocation() throws Exception {
        ActionContext.setContext(invocation.getInvocationContext());
    }

    
    protected void afterInvocation() throws Exception {
        ActionContext.setContext(null);
    }

    
    public Object getAction() {
        return action;
    }

    
    public ActionInvocation getInvocation() {
        return invocation;
    }

    
    public String getResult() {
        return result;
    }

    
    public Exception getException() {
        return exception;
    }

    
    public boolean isDone() {
        return done;
    }
}
