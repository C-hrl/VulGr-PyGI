

package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.CheckboxList;
import org.apache.struts2.components.Component;

import com.opensymphony.xwork2.util.ValueStack;



public class CheckboxListTag extends AbstractRequiredListTag {

    private static final long serialVersionUID = 4023034029558150010L;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new CheckboxList(stack, req, res);
    }
}
