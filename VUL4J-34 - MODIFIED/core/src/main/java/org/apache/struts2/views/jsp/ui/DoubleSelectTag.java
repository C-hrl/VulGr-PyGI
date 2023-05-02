

package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.DoubleSelect;

import com.opensymphony.xwork2.util.ValueStack;


public class DoubleSelectTag extends AbstractDoubleListTag {

    private static final long serialVersionUID = 7426011596359509386L;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new DoubleSelect(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        DoubleSelect doubleSelect = ((DoubleSelect) component);
        doubleSelect.setEmptyOption(emptyOption);
        doubleSelect.setHeaderKey(headerKey);
        doubleSelect.setHeaderValue(headerValue);
        doubleSelect.setMultiple(multiple);
        doubleSelect.setSize(size);

    }
}
