

package org.apache.struts2;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import java.util.Map;



public class ServletActionContext extends ActionContext implements StrutsStatics {

    private static final long serialVersionUID = -666854718275106687L;

    public static final String STRUTS_VALUESTACK_KEY = "struts.valueStack";
    public static final String ACTION_MAPPING = "struts.actionMapping";

    @SuppressWarnings("unused")
    private ServletActionContext(Map context) {
        super(context);
    }

    
    public static ActionContext getActionContext(HttpServletRequest req) {
        ValueStack vs = getValueStack(req);
        if (vs != null) {
            return new ActionContext(vs.getContext());
        } else {
            return null;
        }
    }

    
    public static ValueStack getValueStack(HttpServletRequest req) {
        return (ValueStack) req.getAttribute(STRUTS_VALUESTACK_KEY);
    }

    
    public static ActionMapping getActionMapping() {
        return (ActionMapping) ActionContext.getContext().get(ACTION_MAPPING);
    }

    
    public static PageContext getPageContext() {
        return (PageContext) ActionContext.getContext().get(PAGE_CONTEXT);
    }

    
    public static void setRequest(HttpServletRequest request) {
        ActionContext.getContext().put(HTTP_REQUEST, request);
    }

    
    public static HttpServletRequest getRequest() {
        return (HttpServletRequest) ActionContext.getContext().get(HTTP_REQUEST);
    }

    
    public static void setResponse(HttpServletResponse response) {
        ActionContext.getContext().put(HTTP_RESPONSE, response);
    }

    
    public static HttpServletResponse getResponse() {
        return (HttpServletResponse) ActionContext.getContext().get(HTTP_RESPONSE);
    }

    
    public static ServletContext getServletContext() {
        return (ServletContext) ActionContext.getContext().get(SERVLET_CONTEXT);
    }

    
    public static void setServletContext(ServletContext servletContext) {
        ActionContext.getContext().put(SERVLET_CONTEXT, servletContext);
    }
}
