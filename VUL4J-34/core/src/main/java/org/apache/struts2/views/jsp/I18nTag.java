

package org.apache.struts2.views.jsp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.I18n;

import com.opensymphony.xwork2.util.ValueStack;



public class I18nTag extends ComponentTagSupport {

    private static final long serialVersionUID = -7914587341936116887L;

    protected String name;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new I18n(stack);
    }

    protected void populateParams() {
        super.populateParams();

        ((I18n) component).setName(name);
    }

    public void setName(String name) {
        this.name = name;
    }
}
