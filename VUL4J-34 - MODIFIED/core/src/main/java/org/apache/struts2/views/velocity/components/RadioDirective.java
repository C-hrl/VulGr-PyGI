

package org.apache.struts2.views.velocity.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Radio;

import com.opensymphony.xwork2.util.ValueStack;


public class RadioDirective extends AbstractDirective {
    public String getBeanName() {
        return "radio";
    }

    protected Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Radio(stack, req, res);
    }
}
