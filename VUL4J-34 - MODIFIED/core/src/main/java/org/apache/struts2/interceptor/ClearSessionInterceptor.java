

package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;


public class ClearSessionInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = -2102199238428329238L;

    private static final Logger LOG = LogManager.getLogger(ClearSessionInterceptor.class);

    
    public String intercept(ActionInvocation invocation) throws Exception {
        LOG.debug("Clearing HttpSession");

        ActionContext ac = invocation.getInvocationContext();
        Map session = ac.getSession();
 
        if (null != session) {
            session.clear();
        }
        return invocation.invoke();
    }
}
