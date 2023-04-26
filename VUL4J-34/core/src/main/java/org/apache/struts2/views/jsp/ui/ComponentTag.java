

package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.GenericUIBean;

import com.opensymphony.xwork2.util.ValueStack;


public class ComponentTag extends AbstractUITag {

    private static final long serialVersionUID = 5448365363044104731L;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new GenericUIBean(stack, req, res);
    }
}
