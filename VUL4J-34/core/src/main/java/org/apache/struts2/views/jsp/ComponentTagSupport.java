

package org.apache.struts2.views.jsp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.components.Component;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;


public abstract class ComponentTagSupport extends StrutsBodyTagSupport {
    protected Component component;

    public abstract Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res);

    public int doEndTag() throws JspException {
        component.end(pageContext.getOut(), getBody());
        component = null;
        return EVAL_PAGE;
    }

    public int doStartTag() throws JspException {
        ValueStack stack = getStack();
        component = getBean(stack, (HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse());
        Container container = (Container) stack.getContext().get(ActionContext.CONTAINER);
        container.inject(component);
        
        populateParams();
        boolean evalBody = component.start(pageContext.getOut());

        if (evalBody) {
            return component.usesBody() ? EVAL_BODY_BUFFERED : EVAL_BODY_INCLUDE;
        } else {
            return SKIP_BODY;
        }
    }

    protected void populateParams() {
    }

    public Component getComponent() {
        return component;
    }
}
