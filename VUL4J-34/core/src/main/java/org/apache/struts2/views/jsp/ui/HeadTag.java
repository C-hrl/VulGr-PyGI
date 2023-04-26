

package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Head;

import com.opensymphony.xwork2.util.ValueStack;


public class HeadTag extends AbstractUITag {

    private static final long serialVersionUID = 6876765769175246030L;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Head(stack, req, res);
    }
}
