

package org.apache.struts2.views.jsp.ui;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.Reset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ResetTag extends AbstractUITag {

    private static final long serialVersionUID = 4742704832277392108L;

    protected String action;
    protected String method;
    protected String type;
    protected String src;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Reset(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        Reset reset = ((Reset) component);
        reset.setAction(action);
        reset.setMethod(method);
        reset.setType(type);
        reset.setSrc(src);
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSrc(String src) {
        this.src = src;
    }

}
