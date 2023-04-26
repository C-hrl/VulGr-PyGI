

package org.apache.struts2.views.jsp;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.ApplicationMap;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.RequestMap;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.util.AttributeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import java.util.Map;



public class TagUtils {

    public static ValueStack getStack(PageContext pageContext) {
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
        ValueStack stack = (ValueStack) req.getAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY);

        if (stack == null) {

            HttpServletResponse res = (HttpServletResponse) pageContext.getResponse();
            Dispatcher du = Dispatcher.getInstance();
            if (du == null) {
                throw new ConfigurationException("The Struts dispatcher cannot be found.  This is usually caused by "+
                        "using Struts tags without the associated filter. Struts tags are only usable when the request "+
                        "has passed through its servlet filter, which initializes the Struts dispatcher needed for this tag.");
            }
            stack = du.getContainer().getInstance(ValueStackFactory.class).createValueStack();
            Map<String, Object> extraContext = du.createContextMap(new RequestMap(req),
                    req.getParameterMap(),
                    new SessionMap(req),
                    new ApplicationMap(pageContext.getServletContext()),
                    req,
                    res);
            extraContext.put(ServletActionContext.PAGE_CONTEXT, pageContext);
            stack.getContext().putAll(extraContext);
            req.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);

            
            ActionContext.setContext(new ActionContext(stack.getContext()));
        } else {
            
            Map<String, Object> context = stack.getContext();
            context.put(ServletActionContext.PAGE_CONTEXT, pageContext);

            AttributeMap attrMap = new AttributeMap(context);
            context.put("attr", attrMap);
        }

        return stack;
    }

    public static String buildNamespace(ActionMapper mapper, ValueStack stack, HttpServletRequest request) {
        ActionContext context = new ActionContext(stack.getContext());
        ActionInvocation invocation = context.getActionInvocation();

        if (invocation == null) {
            ActionMapping mapping = mapper.getMapping(request,
                    Dispatcher.getInstance().getConfigurationManager());

            if (mapping != null) {
                return mapping.getNamespace();
            } else {
                
                
                

                String path = RequestUtils.getServletPath(request);
                return path.substring(0, path.lastIndexOf("/"));
            }
        } else {
            return invocation.getProxy().getNamespace();
        }
    }
}
