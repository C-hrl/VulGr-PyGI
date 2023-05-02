

package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.util.TokenHelper;
import org.apache.struts2.views.freemarker.FreemarkerResult;

import javax.servlet.http.HttpSession;
import java.util.Map;



public class ExecuteAndWaitInterceptor extends MethodFilterInterceptor {

    private static final long serialVersionUID = -2754639196749652512L;

    private static final Logger LOG = LogManager.getLogger(ExecuteAndWaitInterceptor.class);

    public static final String KEY = "__execWait";
    public static final String WAIT = "wait";
    protected int delay;
    protected int delaySleepInterval = 100; 
    protected boolean executeAfterValidationPass = false;

    private int threadPriority = Thread.NORM_PRIORITY;

    private Container container;

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    
    protected BackgroundProcess getNewBackgroundProcess(String name, ActionInvocation actionInvocation, int threadPriority) {
        return new BackgroundProcess(name + "BackgroundThread", actionInvocation, threadPriority);
    }

    
    protected String getBackgroundProcessName(ActionProxy proxy) {
        return proxy.getActionName();
    }

    
    protected String doIntercept(ActionInvocation actionInvocation) throws Exception {
        ActionProxy proxy = actionInvocation.getProxy();
        String name = getBackgroundProcessName(proxy);
        ActionContext context = actionInvocation.getInvocationContext();
        Map session = context.getSession();
        HttpSession httpSession = ServletActionContext.getRequest().getSession(true);

        Boolean secondTime  = true;
        if (executeAfterValidationPass) {
            secondTime = (Boolean) context.get(KEY);
            if (secondTime == null) {
                context.put(KEY, true);
                secondTime = false;
            } else {
                secondTime = true;
                context.put(KEY, null);
            }
        }

        
        
        synchronized (httpSession) {
            BackgroundProcess bp = (BackgroundProcess) session.get(KEY + name);

            if ((!executeAfterValidationPass || secondTime) && bp == null) {
                bp = getNewBackgroundProcess(name, actionInvocation, threadPriority);
                session.put(KEY + name, bp);
                performInitialDelay(bp); 
                secondTime = false;
            }

            if ((!executeAfterValidationPass || !secondTime) && bp != null && !bp.isDone()) {
                actionInvocation.getStack().push(bp.getAction());

				final String token = TokenHelper.getToken();
				if (token != null) {
					TokenHelper.setSessionToken(TokenHelper.getTokenName(), token);
                }

                Map results = proxy.getConfig().getResults();
                if (!results.containsKey(WAIT)) {
                	LOG.warn("ExecuteAndWait interceptor has detected that no result named 'wait' is available. " +
                            "Defaulting to a plain built-in wait page. It is highly recommend you " +
                            "provide an action-specific or global result named '{}'.", WAIT);
                    

                    
                    
                    FreemarkerResult waitResult = new FreemarkerResult();
                    container.inject(waitResult);
                    waitResult.setLocation("/org/apache/struts2/interceptor/wait.ftl");
                    waitResult.execute(actionInvocation);

                    return Action.NONE;
                }

                return WAIT;
            } else if ((!executeAfterValidationPass || !secondTime) && bp != null && bp.isDone()) {
                session.remove(KEY + name);
                actionInvocation.getStack().push(bp.getAction());

                
                if (bp.getException() != null) {
                    throw bp.getException();
                }

                return bp.getResult();
            } else {
                
                
                
                
                return actionInvocation.invoke();
            }
        }
    }

    
    protected void performInitialDelay(BackgroundProcess bp) throws InterruptedException {
        if (delay <= 0 || delaySleepInterval <= 0) {
            return;
        }

        int steps = delay / delaySleepInterval;
        LOG.debug("Delaying for {} millis. (using {} steps)", delay, steps);
        int step;
        for (step = 0; step < steps && !bp.isDone(); step++) {
            Thread.sleep(delaySleepInterval);
        }
        LOG.debug("Sleeping ended after {} steps and the background process is {}", step, (bp.isDone() ? " done" : " not done"));
    }

    
    public void setThreadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
    }

    
    public void setDelay(int delay) {
        this.delay = delay;
    }

    
    public void setDelaySleepInterval(int delaySleepInterval) {
        this.delaySleepInterval = delaySleepInterval;
    }

    
    public void setExecuteAfterValidationPass(boolean executeAfterValidationPass) {
        this.executeAfterValidationPass = executeAfterValidationPass;
    }


}
