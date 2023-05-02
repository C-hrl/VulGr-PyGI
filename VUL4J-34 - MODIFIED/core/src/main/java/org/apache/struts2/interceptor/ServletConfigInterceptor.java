

package org.apache.struts2.interceptor;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.interceptor.servlet.ServletPrincipalProxy;
import org.apache.struts2.util.ServletContextAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;



public class ServletConfigInterceptor extends AbstractInterceptor implements StrutsStatics {

    private static final long serialVersionUID = 605261777858676638L;

    
    public String intercept(ActionInvocation invocation) throws Exception {
        final Object action = invocation.getAction();
        final ActionContext context = invocation.getInvocationContext();

        if (action instanceof ServletRequestAware) {
            HttpServletRequest request = (HttpServletRequest) context.get(HTTP_REQUEST);
            ((ServletRequestAware) action).setServletRequest(request);
        }

        if (action instanceof ServletResponseAware) {
            HttpServletResponse response = (HttpServletResponse) context.get(HTTP_RESPONSE);
            ((ServletResponseAware) action).setServletResponse(response);
        }

        if (action instanceof ParameterAware) {
            ((ParameterAware) action).setParameters((Map)context.getParameters());
        }

        if (action instanceof ApplicationAware) {
            ((ApplicationAware) action).setApplication(context.getApplication());
        }
        
        if (action instanceof SessionAware) {
            ((SessionAware) action).setSession(context.getSession());
        }
        
        if (action instanceof RequestAware) {
            ((RequestAware) action).setRequest((Map) context.get("request"));
        }

        if (action instanceof PrincipalAware) {
            HttpServletRequest request = (HttpServletRequest) context.get(HTTP_REQUEST);
            if(request != null) {
                
                ((PrincipalAware) action).setPrincipalProxy(new ServletPrincipalProxy(request));
            }
        }
        if (action instanceof ServletContextAware) {
            ServletContext servletContext = (ServletContext) context.get(SERVLET_CONTEXT);
            ((ServletContextAware) action).setServletContext(servletContext);
        }
        return invocation.invoke();
    }
}
