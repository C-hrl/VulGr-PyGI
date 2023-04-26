

package org.apache.struts2.views.jsp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Include;

import com.opensymphony.xwork2.util.ValueStack;



public class IncludeTag extends ComponentTagSupport {

    private static final long serialVersionUID = -1585165567043278243L;

    protected String value;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Include(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        ((Include) component).setValue(value);
    }

    public void setValue(String value) {
        this.value = value;
    }
}
