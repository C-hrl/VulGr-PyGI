

package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.profiling.UtilTimerStack;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.struts2.StrutsConstants;


public class ProfilingActivationInterceptor extends AbstractInterceptor {

    private String profilingKey = "profiling";
    private boolean devMode;

    
    public String getProfilingKey() {
        return profilingKey;
    }

    
    public void setProfilingKey(String profilingKey) {
        this.profilingKey = profilingKey;
    }
    
    @Inject(StrutsConstants.STRUTS_DEVMODE)
    public void setDevMode(String mode) {
        this.devMode = BooleanUtils.toBoolean(mode);
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        if (devMode) {
            Object val = invocation.getInvocationContext().getParameters().get(profilingKey);
            if (val != null) {
                String sval = (val instanceof String ? (String)val : ((String[])val)[0]);
                boolean enable = BooleanUtils.toBoolean(sval);
                UtilTimerStack.setActive(enable);
                invocation.getInvocationContext().getParameters().remove(profilingKey);
            }
        }
        return invocation.invoke();
    }
}
