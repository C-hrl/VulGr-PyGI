

package org.apache.struts2.views.jsp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Else;

import com.opensymphony.xwork2.util.ValueStack;



public class ElseTag extends ComponentTagSupport {

    private static final long serialVersionUID = 8166807953193406785L;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Else(stack);
    }
}
