

package org.apache.struts2.views.velocity.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.ActionMessage;
import org.apache.struts2.components.Component;

import com.opensymphony.xwork2.util.ValueStack;


public class ActionMessageDirective extends AbstractDirective {
    public String getBeanName() {
        return "actionmessage";
    }

    protected Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new ActionMessage(stack, req, res);
    }
}
