

package org.apache.struts2.views.freemarker.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.ComboBox;
import org.apache.struts2.components.Component;

import com.opensymphony.xwork2.util.ValueStack;


public class ComboBoxModel extends TagModel {
    public ComboBoxModel(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new ComboBox(stack, req, res);
    }
}