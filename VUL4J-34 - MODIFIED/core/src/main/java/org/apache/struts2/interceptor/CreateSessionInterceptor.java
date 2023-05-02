

package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.SessionMap;

import javax.servlet.http.HttpSession;


public class CreateSessionInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = -4590322556118858869L;

    private static final Logger LOG = LogManager.getLogger(CreateSessionInterceptor.class);


    
    public String intercept(ActionInvocation invocation) throws Exception {
        HttpSession httpSession = ServletActionContext.getRequest().getSession(false);
        if (httpSession == null) {
            LOG.debug("Creating new HttpSession and new SessionMap in ServletActionContext");
            ServletActionContext.getRequest().getSession(true);
            ServletActionContext.getContext().setSession(new SessionMap<String, Object>(ServletActionContext.getRequest()));
        }
        return invocation.invoke();
    }

}
