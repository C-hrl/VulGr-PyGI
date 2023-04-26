

package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Checkbox;
import org.apache.struts2.components.Component;

import com.opensymphony.xwork2.util.ValueStack;



public class CheckboxTag extends AbstractUITag {

    private static final long serialVersionUID = -350752809266337636L;

    protected String fieldValue;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Checkbox(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        ((Checkbox) component).setFieldValue(fieldValue);
    }

    public void setFieldValue(String aValue) {
        this.fieldValue = aValue;
    }
}
