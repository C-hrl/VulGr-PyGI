

package org.apache.struts2.views.jsp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.If;

import com.opensymphony.xwork2.util.ValueStack;



public class IfTag extends ComponentTagSupport {

    private static final long serialVersionUID = 4448870162549923833L;

    String test;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new If(stack);
    }

    protected void populateParams() {
        ((If) getComponent()).setTest(test);
    }

    public void setTest(String test) {
        this.test = test;
    }
}
