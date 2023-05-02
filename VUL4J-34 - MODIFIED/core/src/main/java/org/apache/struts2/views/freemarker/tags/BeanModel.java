

package org.apache.struts2.views.freemarker.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Bean;
import org.apache.struts2.components.Component;

import com.opensymphony.xwork2.util.ValueStack;


public class BeanModel extends TagModel {
    public BeanModel(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new Bean(stack);
    }
}
