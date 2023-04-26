

package org.apache.struts2.views.velocity.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Form;

import com.opensymphony.xwork2.util.ValueStack;


public class FormDirective extends AbstractDirective {
    protected Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Form(stack, req, res);
    }

    public String getBeanName() {
        return "form";
    }

    public int getType() {
        return BLOCK;
    }
}
