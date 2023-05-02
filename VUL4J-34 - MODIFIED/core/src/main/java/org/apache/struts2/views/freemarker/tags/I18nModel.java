

package org.apache.struts2.views.freemarker.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.I18n;

import com.opensymphony.xwork2.util.ValueStack;


public class I18nModel extends TagModel {
    public I18nModel(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new I18n(stack);
    }
}
