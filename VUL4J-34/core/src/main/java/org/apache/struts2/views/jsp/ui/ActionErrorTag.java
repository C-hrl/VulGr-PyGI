

package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.ActionError;
import org.apache.struts2.components.Component;

import com.opensymphony.xwork2.util.ValueStack;


public class ActionErrorTag extends AbstractUITag {

    private static final long serialVersionUID = -3710234378022378639L;

    private boolean escape = true;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new ActionError(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        ActionError error = (ActionError) component;
        error.setEscape(escape);
    }

    public void setEscape(boolean escape) {
        this.escape = escape;
    }
}
