

package org.apache.struts2.views.freemarker.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Submit;

import com.opensymphony.xwork2.util.ValueStack;


public class SubmitModel extends TagModel {
    public SubmitModel(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new Submit(stack, req, res);
    }
}
