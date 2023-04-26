

package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Div;

import com.opensymphony.xwork2.util.ValueStack;

public class DivTag extends AbstractClosingTag {

    private static final long serialVersionUID = 5309231035916461758L;
    
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Div(stack, req, res);
    }
}
