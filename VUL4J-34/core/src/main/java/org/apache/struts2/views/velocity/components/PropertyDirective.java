

package org.apache.struts2.views.velocity.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Property;

import com.opensymphony.xwork2.util.ValueStack;


public class PropertyDirective extends AbstractDirective {
    public String getBeanName() {
        return "property";
    }

    protected Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Property(stack);
    }
}
