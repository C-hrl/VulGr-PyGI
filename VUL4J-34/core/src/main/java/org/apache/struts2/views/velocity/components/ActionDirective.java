

package org.apache.struts2.views.velocity.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.ActionComponent;
import org.apache.struts2.components.Component;

import com.opensymphony.xwork2.util.ValueStack;


public class ActionDirective extends AbstractDirective {
    public String getBeanName() {
        return "action";
    }

    protected Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new ActionComponent(stack, req, res);
    }
}
