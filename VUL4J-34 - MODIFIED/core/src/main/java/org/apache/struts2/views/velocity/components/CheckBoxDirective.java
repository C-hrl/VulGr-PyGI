

package org.apache.struts2.views.velocity.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Checkbox;
import org.apache.struts2.components.Component;

import com.opensymphony.xwork2.util.ValueStack;


public class CheckBoxDirective extends AbstractDirective {
    protected Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Checkbox(stack, req, res);
    }

    public String getBeanName() {
        return "checkbox";
    }
}
