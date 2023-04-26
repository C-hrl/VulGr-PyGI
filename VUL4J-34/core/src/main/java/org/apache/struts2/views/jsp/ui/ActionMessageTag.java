

package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.ActionMessage;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.ActionError;

import com.opensymphony.xwork2.util.ValueStack;


public class ActionMessageTag extends AbstractUITag {

    private static final long serialVersionUID = 243396927554182506L;

    private boolean escape = true;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new ActionMessage(stack, req, res);
    }

     protected void populateParams() {
        super.populateParams();

        ActionMessage message = (ActionMessage) component;
        message.setEscape(escape);
    }

    public void setEscape(boolean escape) {
        this.escape = escape;
    }
}
