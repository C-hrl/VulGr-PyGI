

package org.apache.struts2.views.jsp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Push;

import com.opensymphony.xwork2.util.ValueStack;



public class PushTag extends ComponentTagSupport {

    private static final long serialVersionUID = -1357895305148907931L;

    protected String value;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Push(stack);
    }

    protected void populateParams() {
        super.populateParams();

        ((Push) component).setValue(value);
    }

    public void setValue(String value) {
        this.value = value;
    }
}
