

package org.apache.struts2.views.velocity.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Anchor;
import org.apache.struts2.components.Component;

import com.opensymphony.xwork2.util.ValueStack;


public class AnchorDirective extends AbstractDirective {
    public String getBeanName() {
        return "a";
    }

    protected Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Anchor(stack, req, res);
    }

    public int getType() {
        return BLOCK;
    }
}
