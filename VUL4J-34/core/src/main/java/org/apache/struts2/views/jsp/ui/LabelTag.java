

package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Label;

import com.opensymphony.xwork2.util.ValueStack;



public class LabelTag extends AbstractUITag {

    private static final long serialVersionUID = 4008321310097730458L;

    protected String forAttr;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Label(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        ((Label) component).setFor(forAttr);
    }

    public void setFor(String aFor) {
        this.forAttr = aFor;
    }
}
