

package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.ComboBox;
import org.apache.struts2.components.Component;

import com.opensymphony.xwork2.util.ValueStack;


public class ComboBoxTag extends TextFieldTag {

    private static final long serialVersionUID = 3509392460170385605L;

    protected String list;
    protected String listKey;
    protected String listValue;
    protected String headerKey;
    protected String headerValue;
    protected String emptyOption;

    public void setEmptyOption(String emptyOption) {
        this.emptyOption = emptyOption;
    }

    public void setHeaderKey(String headerKey) {
        this.headerKey = headerKey;
    }

    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    public void setListKey(String listKey) {
        this.listKey = listKey;
    }

    public void setListValue(String listValue) {
        this.listValue = listValue;
    }

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new ComboBox(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        ((ComboBox) component).setList(list);
        ((ComboBox) component).setListKey(listKey);
        ((ComboBox) component).setListValue(listValue);
        ((ComboBox) component).setHeaderKey(headerKey);
        ((ComboBox) component).setHeaderValue(headerValue);
        ((ComboBox) component).setEmptyOption(emptyOption);
    }

    public void setList(String list) {
        this.list = list;
    }
}
