

package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.TextArea;

import com.opensymphony.xwork2.util.ValueStack;



public class TextareaTag extends AbstractUITag {

    private static final long serialVersionUID = -4107122506712927927L;

    protected String cols;
    protected String readonly;
    protected String rows;
    protected String wrap;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new TextArea(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        TextArea textArea = ((TextArea) component);
        textArea.setCols(cols);
        textArea.setReadonly(readonly);
        textArea.setRows(rows);
        textArea.setWrap(wrap);
    }

    public void setCols(String cols) {
        this.cols = cols;
    }

    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }

    public void setRows(String rows) {
        this.rows = rows;
    }

    public void setWrap(String wrap) {
        this.wrap = wrap;
    }

}
