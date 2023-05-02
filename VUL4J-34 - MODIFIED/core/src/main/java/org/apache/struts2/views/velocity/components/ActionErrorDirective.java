

package org.apache.struts2.views.velocity.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.ActionError;
import org.apache.struts2.components.Component;

import com.opensymphony.xwork2.util.ValueStack;


public class ActionErrorDirective extends AbstractDirective {
    public String getBeanName() {
        return "actionerror";
    }

    protected Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new ActionError(stack, req, res);
    }
}
