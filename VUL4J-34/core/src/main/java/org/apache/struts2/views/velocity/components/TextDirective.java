

package org.apache.struts2.views.velocity.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Text;

import com.opensymphony.xwork2.util.ValueStack;


public class TextDirective extends AbstractDirective {
    public String getBeanName() {
        return "text";
    }

    protected Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Text(stack);
    }
}
