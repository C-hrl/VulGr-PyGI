

package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.FieldError;

import com.opensymphony.xwork2.util.ValueStack;


public class FieldErrorTag extends AbstractUITag {

    private static final long serialVersionUID = -182532967507726323L;

    protected String fieldName;
    protected boolean escape = true;


    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new FieldError(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        FieldError fieldError = ((FieldError) component);
        fieldError.setFieldName(this.fieldName);
        fieldError.setEscape(escape);
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setEscape(boolean escape) {
        this.escape = escape;
    }
}

