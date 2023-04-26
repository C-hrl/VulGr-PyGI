

package org.apache.struts2.views.jsp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.ElseIf;

import com.opensymphony.xwork2.util.ValueStack;


public class ElseIfTag extends ComponentTagSupport {

    private static final long serialVersionUID = -3872016920741400345L;

    protected String test;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new ElseIf(stack);
    }

    protected void populateParams() {
        ((ElseIf) getComponent()).setTest(test);
    }

    public void setTest(String test) {
        this.test = test;
    }
}
