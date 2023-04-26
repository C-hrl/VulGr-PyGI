

package org.apache.struts2.views.freemarker.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Hidden;

import com.opensymphony.xwork2.util.ValueStack;


public class HiddenModel extends TagModel {
    public HiddenModel(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new Hidden(stack, req, res);
    }
}
