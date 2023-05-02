

package org.apache.struts2.views.velocity.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Div;

import com.opensymphony.xwork2.util.ValueStack;


public class DivDirective extends AbstractDirective {
    public String getBeanName() {
        return "div";
    }

    protected Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Div(stack, req, res);
    }

    public int getType() {
        return BLOCK;
    }
}
