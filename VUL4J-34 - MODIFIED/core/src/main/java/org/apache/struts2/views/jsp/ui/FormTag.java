

package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Form;

import com.opensymphony.xwork2.util.ValueStack;



public class FormTag extends AbstractClosingTag {

    private static final long serialVersionUID = 2792301046860819658L;

    protected String action;
    protected String target;
    protected String enctype;
    protected String method;
    protected String namespace;
    protected String validate;
    protected String onsubmit;
    protected String onreset;
    protected String portletMode;
    protected String windowState;
    protected String acceptcharset;
    protected String focusElement;
    protected boolean includeContext = true;
    
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Form(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();
        Form form = ((Form) component);
        form.setAction(action);
        form.setTarget(target);
        form.setEnctype(enctype);
        form.setMethod(method);
        form.setNamespace(namespace);
        form.setValidate(validate);
        form.setOnreset(onreset);
        form.setOnsubmit(onsubmit);
        form.setPortletMode(portletMode);
        form.setWindowState(windowState);
        form.setAcceptcharset(acceptcharset);
        form.setFocusElement(focusElement);
        form.setIncludeContext(includeContext);
    }


    public void setAction(String action) {
        this.action = action;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setEnctype(String enctype) {
        this.enctype = enctype;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setValidate(String validate) {
        this.validate = validate;
    }

    public void setOnsubmit(String onsubmit) {
        this.onsubmit = onsubmit;
    }

    public void setOnreset(String onreset) {
        this.onreset = onreset;
    }

    public void setPortletMode(String portletMode) {
        this.portletMode = portletMode;
    }

    public void setWindowState(String windowState) {
        this.windowState = windowState;
    }

    public void setAcceptcharset(String acceptcharset) {
        this.acceptcharset = acceptcharset;
    }

    public void setFocusElement(String focusElement) {
        this.focusElement = focusElement;
    }

    public void setIncludeContext(boolean includeContext) {
        this.includeContext = includeContext;
    }
}
