

package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Hidden;

import com.opensymphony.xwork2.util.ValueStack;



public class HiddenTag extends AbstractUITag {

    private static final long serialVersionUID = -1124367972048371675L;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Hidden(stack, req, res);
    }
}
