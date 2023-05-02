

package org.apache.struts2.views.velocity.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.DoubleSelect;

import com.opensymphony.xwork2.util.ValueStack;


public class DoubleSelectDirective extends AbstractDirective {
    protected Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new DoubleSelect(stack, req, res);
    }

    public String getBeanName() {
        return "doubleselect";
    }
}
