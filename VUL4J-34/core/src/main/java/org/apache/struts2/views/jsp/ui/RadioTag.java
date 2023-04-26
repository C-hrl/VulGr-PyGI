

package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Radio;

import com.opensymphony.xwork2.util.ValueStack;



public class RadioTag extends AbstractRequiredListTag {

    private static final long serialVersionUID = -6497403399521333624L;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Radio(stack, req, res);
    }
}
