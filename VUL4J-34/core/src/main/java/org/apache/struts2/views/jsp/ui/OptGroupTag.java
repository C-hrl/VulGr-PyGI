

package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.OptGroup;
import org.apache.struts2.views.jsp.ComponentTagSupport;

import com.opensymphony.xwork2.util.ValueStack;


public class OptGroupTag extends ComponentTagSupport {

    private static final long serialVersionUID = 7367401003498678762L;

    protected String list;
    protected String label;
    protected String disabled;
    protected String listKey;
    protected String listValue;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new OptGroup(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        OptGroup optGroup = (OptGroup) component;
        optGroup.setList(list);
        optGroup.setLabel(label);
        optGroup.setDisabled(disabled);
        optGroup.setListKey(listKey);
        optGroup.setListValue(listValue);
    }

    public void setList(String list) {
        this.list = list;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    public void setListKey(String listKey) {
        this.listKey = listKey;
    }

    public void setListValue(String listValue) {
        this.listValue = listValue;
    }
}
